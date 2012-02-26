package com.jeffskj.torrent;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.log.DownloadLog;
import com.jeffskj.torrent.log.LogEntry;
import com.jeffskj.torrent.log.LogEntryDAO;

public class TorrentOrganizerTest
{
    private static final Configuration CONFIG = getTestConfig();
    private static TorrentOrganizer organizer = new TorrentOrganizer(CONFIG, createMockedDownloadLog());

    private static Configuration getTestConfig()
    {
        Configuration config = new Configuration();
        Episode ep = new Episode(13);
        ep.setTitle("Big Baby");
        config.addShow(new TVShow("House").addSeason(new Season(5).addEpisode(ep)));
        
        ep = new Episode(3);
        ep.setTitle("14 Chefs Compete");
        config.addShow(new TVShow("Hell's Kitchen (US)").addSeason(new Season(5).addEpisode(ep)));
        
        config.setFinishedDownloadLocation(FileUtils.toFile(TorrentOrganizerTest.class.getResource(
            "/test-downloaded/House.S05E13.Xvid.[lol].avi")).getParentFile());
        
        config.setVideoLibraryLocation(new File(config.getFinishedDownloadLocation().getParentFile(), "test-library"));
        
        return config;
    }

    private static DownloadLog createMockedDownloadLog()
    {
        LogEntryDAO mock = EasyMock.createMock(LogEntryDAO.class);
        EasyMock.expect(mock.findAll()).andReturn(new ArrayList<LogEntry>()).anyTimes();
        mock.insert(EasyMock.isA(LogEntry.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(mock);
        return new DownloadLog(mock);
    }

    @Test
    public void organizeTestFolder() throws IOException
    {
        File copied = new File(CONFIG.getFinishedDownloadLocation(), "House.S05E13.Xvid.[lol].avi.copied");
        copied.delete();
        FileUtils.deleteDirectory(CONFIG.getVideoLibraryLocation());
        
        organizer.organizeDownloads();
        assertTrue(new File(CONFIG.getVideoLibraryLocation(), 
                            "TV Shows/House/Season 05/House - S05E13 - Big Baby - XviD.avi").exists());
        assertTrue(copied.exists());
    }

    @After
    public void cleanup() throws IOException, URISyntaxException
    {
        File root = new File(getClass().getResource("/torrent-config.xml").toURI()).getParentFile();
        FileUtils.deleteDirectory(new File(root, "TV Shows"));
        FileUtils.deleteQuietly(new File(root, "House.S05E13.HDTV.XviD-LOL.avi"));
    }
    
    @Test
    public void extractRarFiles() throws URISyntaxException, IOException
    {
        //if this throws a null pointer exception, make eclipse do a clean
        File outputFolder = new File(getClass().getResource("/rar/test.rar").toURI()).getParentFile();
        organizer.extractRarFiles(outputFolder);
        
        File avi = new File(outputFolder.getParentFile(), "test.avi");
        try
        {
            assertTrue(avi.exists());
        }
        finally
        {
            avi.delete();
        }
    }    
    
    @Test
    public void deleteMarkedFiles()
    {
        organizer.deleteMarkedFiles(FileUtils.toFile(getClass().getResource("/to-delete.delete")).getParentFile());
        assertNull(getClass().getResource("/to-delete.delete"));
    }
}
