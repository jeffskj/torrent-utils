package com.jeffskj.torrent.showinfo;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;

public class TVRageShowInfoProvider implements ShowInfoProvider {
    private Logger log = LoggerFactory.getLogger(TVRageShowInfoProvider.class);

    private HttpClient client;

    @Inject
    public TVRageShowInfoProvider(HttpClient client) {
        this.client = client;
    }

    public TVShow getShowInfo(String name) {
        log.info("getting show info for {}", name);

        ConvertUtils.register(getDateConverter(), Date.class);
        try {
            int showid = getShowId(name);
            if (showid == -1) {
                return null;
            } // no show by that name
            Response response = client.get("http://www.tvrage.com/feeds/full_show_info.php?sid=" + showid);

            log.debug("Found TV Show named {} at http://www.tvrage.com/feeds/full_show_info.php", name);

            Digester digest = new Digester();
            digest.addObjectCreate("Show", TVShow.class);
            digest.addBeanPropertySetter("Show/name");
            digest.addBeanPropertySetter("Show/showid", "id");
            digest.addBeanPropertySetter("Show/started", "startedDate");
            digest.addBeanPropertySetter("Show/origin_country", "country");
            digest.addBeanPropertySetter("Show/status");
            digest.addBeanPropertySetter("Show/runtime");
            digest.addObjectCreate("Show/Episodelist/Season", Season.class);
            digest.addSetProperties("Show/Episodelist/Season", "no", "number");
            digest.addSetNext("Show/Episodelist/Season", "addSeason");
            digest.addObjectCreate("Show/Episodelist/Season/episode", Episode.class);
            digest.addSetNext("Show/Episodelist/Season/episode", "addEpisode");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/seasonnum", "episodeNum");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/epnum", "absoluteEpisodeNum");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/prodnum", "productionNum");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/airdate", "airDate");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/link", "detailsLink");
            digest.addBeanPropertySetter("Show/Episodelist/Season/episode/title");
            return (TVShow) digest.parse(response.getBody());
        } catch (Exception e) {
            log.error("error fetching TV show data for show " + name, e);
            return null;
        } 
    }

    private Converter getDateConverter() {
        return new Converter() {
            DateConverter delegate = new DateConverter();
            {
                delegate.setPatterns(new String[]{"yyyy-MM-dd", "MMMM/dd/yyyy"});
            } // initializer

            public Object convert(Class type, Object value) {
                try {
                    return delegate.convert(type, value);
                } catch (ConversionException ex) {
                    return new Date();
                }
            }

        };
    }

    private int getShowId(String name) {
        try {
            Response response = client.get("http://www.tvrage.com/feeds/search.php?show=" + URLEncoder.encode(name, "utf-8"));
            String responseString = response.getBodyAsString();
            String sid = StringUtils.substringBefore(StringUtils.substringAfter(responseString, "<showid>"), "</showid>");
            if (sid == null) {
                return -1;
            }
            log.debug("found id: {} for show: {}", sid, name);
            return Integer.parseInt(sid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }

}
