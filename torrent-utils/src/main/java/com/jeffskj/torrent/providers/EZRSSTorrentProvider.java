package com.jeffskj.torrent.providers;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;
import com.jeffskj.torrent.http.SimpleHttpClient;
import com.jeffskj.torrent.util.UrlUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class EZRSSTorrentProvider implements TorrentProvider {
    private static Logger log = LoggerFactory.getLogger(EZRSSTorrentProvider.class);
    private static String URL = "http://ezrss.it/search/index.php?show_name=${show}&show_name_exact=true&date=&quality=HDTV&quality_exact=true&release_group=&mode=rss";

    private Cache<String, SyndFeed> cache = createCache();
    
    private Pattern ATTRIBUTES_PATTERN = Pattern.compile("Season:\\s*(\\d+);\\s*Episode:\\s*(\\d+)");
    private final HttpClient httpClient;

    @Inject
    public EZRSSTorrentProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private Cache<String, SyndFeed> createCache() {
        return CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new FeedFetcher());
    }

    public EZRSSTorrentProvider() {
        this(new SimpleHttpClient());
    }

    @SuppressWarnings("unchecked")
    public SortedSet<Torrent> getTorrents(Configuration config, Episode episode) {
        log.info("searching ezrss for torrents for {}", episode);
        SortedSet<Torrent> torrents = new TreeSet<Torrent>();

        SyndFeed feed = cache.getUnchecked(getShowName(episode));
        if (feed == null) {
            log.info("show {} not found", episode.getShowName());
        }
        
        for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
            if (episode.matches(entry.getTitle())) {
                torrents.add(new Torrent(entry.getLink(), -1.0, -1, -1));
            }
            Matcher descriptionMatcher = ATTRIBUTES_PATTERN.matcher(entry.getDescription().getValue());
            if (descriptionMatcher.find()) {
                int season = Integer.parseInt(descriptionMatcher.group(1));
                int epNum = Integer.parseInt(descriptionMatcher.group(2));
                if (episode.getSeason().getNumber() == season && episode.getEpisodeNum() == epNum) {
                    torrents.add(new Torrent(entry.getLink(), -1, -1, -1));
                }
            }
        }

        log.info("found {} torrents from ezrss", torrents.size());
        return torrents;
    }

    public static String getURL(String showName) {
        return URL.replace("${show}", UrlUtils.encode(showName));
    }

    private String getShowName(Episode ep) {
        return StringUtils.replaceChars(ep.getShowName(), "()'", "");
    }

    private class FeedFetcher extends CacheLoader<String, SyndFeed> {
        private static final int MAX_TRIES = 5;
        
        private SyndFeedInput feedInput = new SyndFeedInput();
        
        @Override
        public SyndFeed load(String showName) throws Exception {
            return load(showName, 0);
        }
        
        public SyndFeed load(String showName, int tries) {
            if (tries >= MAX_TRIES) {
                log.warn("failed reading feed for show {} after {} tries", showName, tries);
                return null;
            }
            
            log.info("fetching ezrss feed for show {}", showName);
            XmlReader reader = null;
            Response response = null;
            try {
                String uri = getURL(showName);
                log.debug("searching on uri {}", uri);
                response = httpClient.get(uri);
                reader = new XmlReader(response.getBody());
                return feedInput.build(reader);
            } catch (Exception e) {
                log.error("Error reading ezrss feed", e);
                if (response != null) {
                    log.debug("response body: ", response.getBodyAsString());
                }
                return load(showName, tries++);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }

}
