package com.jeffskj.torrent.log;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.log.LogEntry.Status;

public class DownloadLogTest
{
    @Test
    public void testAddDownload()
    {
        LogEntryDAO dao = EasyMock.createMock(LogEntryDAO.class);
        EasyMock.expect(dao.findAll()).andReturn(new ArrayList<LogEntry>());
        dao.insert(EasyMock.isA(LogEntry.class));
        EasyMock.expectLastCall().times(3);
        EasyMock.replay(dao);
        
        DownloadLog downloads = new DownloadLog(dao);
        TVShow show = new TVShow("Lie To Me");
        Season season = new Season(1);
        Episode episode = new Episode(2);
        season.addEpisode(episode);
        show.addSeason(season);
        
        downloads.addDownload(episode, null);
        assertTrue(downloads.alreadyDownloaded(episode));
        
        season.addEpisode(episode = new Episode(3));
        List<Episode> episodes = downloads.getNextEpisodes(show);
        assertEquals(episode.getIdString(), episodes.get(0).getIdString());
        
        downloads.addDownload(episode, null);
        assertTrue(downloads.alreadyDownloaded(episode));
        
        show = new TVShow("House");
        season = new Season(1);
        episode = new Episode(2);
        season.addEpisode(episode);
        show.addSeason(season);
        
        downloads.addDownload(episode, null);
        assertTrue(downloads.alreadyDownloaded(episode));
        
        assertFalse(downloads.isInvalid(episode, null));
        
        episode = new Episode(3);
        episode.setSeason(season);
        assertFalse(downloads.isInvalid(episode, null));
    }
    
    @Test
    public void readExistingLog()
    {
        String invalidUrl = "http://torrents.thepiratebay.org/4876384/The.Office.S05E24.HDTV.XviD-LOL.%5BVTV%5D.avi.4876384.TPB.torrent";
        ArrayList<LogEntry> entries = new ArrayList<LogEntry>();
        entries.add(new LogEntry("The Office S02E01", Status.SUCCESS, null));
        entries.add(new LogEntry("The Office S05E13", Status.SUCCESS, null));
        entries.add(new LogEntry("The Office S05E24", Status.INVALID, invalidUrl));
        
        LogEntryDAO dao = EasyMock.createMock(LogEntryDAO.class);
        EasyMock.expect(dao.findAll()).andReturn(entries);
        EasyMock.replay(dao);
        
        DownloadLog downloads = new DownloadLog(dao);
        Episode episode = new Episode(1);
        new TVShow("The Office").addSeason(new Season(2).addEpisode(episode));
        assertTrue(downloads.alreadyDownloaded(episode));
        
        episode = new Episode(13);
        new TVShow("The Office").addSeason(new Season(5).addEpisode(episode));
        assertTrue(downloads.alreadyDownloaded(episode));
        
        episode = new Episode(24);
        new TVShow("The Office").addSeason(new Season(5).addEpisode(episode));
        assertTrue(downloads.isInvalid(episode, invalidUrl));
    }
}
