package com.jeffskj.torrent.showinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;

public class XMLShowInfoCacheTest
{
    private File cacheDir;

    @Before
    public void setup() throws IOException
    {
        File targetDir = FileUtils.toFile(getClass().getResource("/marker.file")).getParentFile().getParentFile();
        cacheDir = new File(targetDir, "test-resources" + XMLShowInfoCache.CACHE_DIR);
    }
    
    @Test
    public void addToCacheAndReadBackFromItThenEjectIt()
    {
        ShowInfoCache cache = new XMLShowInfoCache(cacheDir);
        TVShow show = getTestShow();
        cache.put(show);
        
        cache = new XMLShowInfoCache(cacheDir);
        show = cache.get("House");
        assertEquals(12345, show.getId());
        assertEquals(60, show.getRuntime());
        
        cache.eject(show);
        cache = new XMLShowInfoCache(cacheDir);
        show = cache.get("House");
        assertNull(show);
    }

    private TVShow getTestShow()
    {
        TVShow show = new TVShow();
        show.setId(12345);
        show.setName("House");
        show.setRuntime(60);
        Episode episode = new Episode();
        episode.setAirDate(new Date());
        episode.setEpisodeNum(1);
        episode.setTitle("crap");
        return show.addSeason(new Season(1).addEpisode(episode));
    }
    
    @After
    public void cleanup() throws IOException
    {
        FileUtils.deleteDirectory(cacheDir);
    }
}
