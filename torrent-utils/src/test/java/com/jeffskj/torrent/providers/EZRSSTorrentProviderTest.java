package com.jeffskj.torrent.providers;

import static org.junit.Assert.assertTrue;

import java.util.SortedSet;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;
import com.jeffskj.torrent.http.SimpleHttpClient;

public class EZRSSTorrentProviderTest extends EasyMockSupport {
    @Test
    @Ignore
    public void tryToGetATorrent() {
        Episode ep = new Episode(1);
        new TVShow("House").addSeason(new Season(7).addEpisode(ep));

        SortedSet<Torrent> torrents = new EZRSSTorrentProvider(new SimpleHttpClient()).getTorrents(null, ep);
        System.out.println(torrents);
        assertTrue(torrents.size() > 0);
    }

    @Test
    public void cachesResults() {
        Episode ep = new Episode(1);
        new TVShow("House").addSeason(new Season(6).addEpisode(ep));

        String responseBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel/></rss>";

        HttpClient mockClient = createMock(HttpClient.class);
        
        EasyMock.expect(mockClient.get(EasyMock.isA(String.class))).andReturn(new Response(responseBody, ""));
        
        replayAll();
        
        EZRSSTorrentProvider provider = new EZRSSTorrentProvider(mockClient);
        for (int i = 0; i < 10; i++) {
            provider.getTorrents(null, ep);
        }
        EasyMock.verify(mockClient);
    }
}
