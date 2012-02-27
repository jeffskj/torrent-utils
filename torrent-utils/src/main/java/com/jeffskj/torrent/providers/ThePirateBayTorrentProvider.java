package com.jeffskj.torrent.providers;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.jeffskj.torrent.StringUtils;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;
import com.jeffskj.torrent.http.SimpleHttpClient;
import com.jeffskj.torrent.util.UrlUtils;

public class ThePirateBayTorrentProvider implements TorrentProvider
{
    public static final String APPROVED_USERS_PROPERTY = "piratebay.users.approved";
    
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String BASE_SEARCH_URL = "http://thepiratebay.se/search/";
    private static final Pattern SIZE_PATTERN = Pattern.compile("Size (\\d+\\.\\d{2})");
    private HttpClient http;
    
    @Inject
    public ThePirateBayTorrentProvider(HttpClient http) {
        this.http = http;
    }
    
    public ThePirateBayTorrentProvider() {
        this(new SimpleHttpClient());
    }
    
    public SortedSet<Torrent> getTorrents(Configuration config, Episode episode)
    {
        SortedSet<Torrent> torrents = new TreeSet<Torrent>();
        Response response = http.get(BASE_SEARCH_URL + UrlUtils.encode(episode.getIdString().replace("'", "")), false);
        if (response != null)
        {
            Set<String> approvedUsers = Sets.newHashSet(Splitter.on(',').split(config.getProperties().get(APPROVED_USERS_PROPERTY)));
            log.debug("using approved TPB users list {}", approvedUsers);
            Document document = Jsoup.parse(response.getBodyAsString());
            Elements downloadLinks = document.getElementsByAttributeValue("title", "Download this torrent using magnet");
            for (Element e : downloadLinks) {
                String torrentName = e.previousElementSibling().child(0).text();
                if (episode.matches(torrentName)) {
                    Elements userLink = e.parent().getElementsByAttributeValueContaining("href", "user");
                    if (userLink.isEmpty()) {
                        log.debug("ignoring Anonymous user");
                        continue;
                    }
                    String userName = userLink.last().text();
                    if (approvedUsers.contains(userName)) {
                        Element row = e.parent().parent();
                        torrents.add(parseTorrent(row));
                    }
                }
            }
        }
        
        return torrents;
    }

    private Torrent parseTorrent(Element row) {
        Element description = row.child(1);
        String detailsString = description.getElementsContainingOwnText("Size ").text();
        return new Torrent(description.child(1).attr("href"), 
                NumberUtils.toDouble(StringUtils.firstMatchingGroup(SIZE_PATTERN, detailsString)), 
                NumberUtils.toInt(row.child(2).text()), 
                NumberUtils.toInt(row.child(3).text()));
    }
}