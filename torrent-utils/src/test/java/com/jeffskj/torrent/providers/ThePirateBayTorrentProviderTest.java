package com.jeffskj.torrent.providers;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;
import com.jeffskj.torrent.http.SimpleHttpClient;
import com.jeffskj.torrent.testing.AbstractTVShowTest;



public class ThePirateBayTorrentProviderTest extends AbstractTVShowTest
{
    @Test
    public void tryToGetATorrent() throws IOException 
    {
        HttpClient client = EasyMock.createMock(HttpClient.class);
        Response r = new Response(IOUtils.toString(getTestResource("tpb-results.txt")), "");
        EasyMock.expect(client.get("http://thepiratebay.se/search/House+S08E04", false)).andReturn(r);
        EasyMock.replay(client);
        
        Episode episode = getEpisode("House", 8, 4);
        Configuration config = new Configuration();
        Properties props = new Properties();
        props.setProperty(ThePirateBayTorrentProvider.APPROVED_USERS_PROPERTY, "eztv,VTV,TvTeam");
        config.setProperties(props);
        
        SortedSet<Torrent> torrents = new ThePirateBayTorrentProvider(client).getTorrents(config, episode);
        System.out.println(torrents);
        assertEquals(3, torrents.size());    
    }
    
    @Test
    @Ignore
    public void tryToGetATorrentIT() throws IOException 
    {
        Episode episode = getEpisode("House", 8, 4);
        Configuration config = new Configuration();
        Properties props = new Properties();
        props.setProperty(ThePirateBayTorrentProvider.APPROVED_USERS_PROPERTY, "eztv,VTV,TvTeam");
        config.setProperties(props);
        
        SortedSet<Torrent> torrents = new ThePirateBayTorrentProvider(new SimpleHttpClient()).getTorrents(config, episode);
        System.out.println(torrents);
        assertEquals(3, torrents.size());    
    }

}
