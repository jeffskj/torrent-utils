package com.jeffskj.torrent.config;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.jeffskj.torrent.providers.EZRSSTorrentProvider;

public class ConfigurationManagerTest
{
    @Test
    public void loadConfiguration()
    {
        Configuration config = new ConfigurationManager().load();
        
        validateConfig(config);
        System.out.println(config);
        
        config = new Configuration();
        config.addShow(new TVShow("The Office"));
        config.addShow(new TVShow("The Big Bang Theory"));
        config.addShow(new TVShow("House"));
        TVShow show = new TVShow("Hells Kitchen US");
        show.setStartSeason(5);
        config.addShow(show);
        config.addShow(new TVShow("Family Guy"));
        
        config.setFinishedDownloadLocation(new File("/torrent-download/finished"));
        config.setTorrentDownloadLocation(new File("/torrent-download/downloads"));
        config.setVideoLibraryLocation(new File("/library/videos"));
        config.addProvider(new EZRSSTorrentProvider(null));
    }

    private void validateConfig(Configuration config)
    {
        assertEquals(5, config.getShows().size());
        assertEquals(1, config.getTorrentProviders().size());
        assertEquals("The Office", config.getShows().get(0).getName());
        assertEquals("Hells Kitchen US", config.getShows().get(3).getName());
        assertEquals(5, config.getShows().get(3).getStartSeason());
        
        assertEquals(EZRSSTorrentProvider.class, config.getTorrentProviders().get(0).getClass());
        assertEquals(new File("/torrent-download/finished"), config.getFinishedDownloadLocation());
        assertEquals(new File("/torrent-download/downloads"), config.getTorrentDownloadLocation());
        assertEquals(new File("/library/videos"), config.getVideoLibraryLocation());
        assertEquals("eztv,someuser", config.getProperties().get("piratebay.users.approved"));
    }
}