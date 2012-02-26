package com.jeffskj.torrent.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.log.LogEntry.Status;

public class DownloadLog
{
    private Logger log = LoggerFactory.getLogger(DownloadLog.class);
    private SortedMap<String, LogEntry> downloadedShows = new TreeMap<String, LogEntry>();
    private LogEntryDAO dao;
    
    @Inject
    public DownloadLog(LogEntryDAO dao) 
    {
        this.dao = dao;
        populate();
    }

    private void populate()
    {
        for (LogEntry entry : dao.findAll())
        {
            downloadedShows.put(entry.getShowId(), entry);
        }
    }

    public void addDownload(Episode episode, String url)
    {
        log.info("adding episode: {} to the download log", episode.getIdString());
        saveOrUpdate(episode, Status.SUCCESS, url);
    }
    
    public void addAttempt(Episode episode)
    {
        log.info("adding attempt: {} to the download log", episode.getIdString());
        saveOrUpdate(episode, Status.NOTFOUND, null);
    }
    
    public void addFailure(Episode episode)
    {
        log.info("adding failure: {} to the download log", episode.getIdString());
        saveOrUpdate(episode, Status.FAILURE, null);
    }
    
    public void setCompleted(Episode episode)
    {
        log.info("setting: {} to completed status", episode.getIdString());
        saveOrUpdate(episode, Status.COMPLETED, null);
    }
    
    private void saveOrUpdate(Episode ep, Status status, String url) 
    {
        String id = ep.getIdString();
        if (downloadedShows.containsKey(id))
        {
            LogEntry entry = downloadedShows.get(id);
            entry.setStatus(status);
            entry.setUrl(url);
            dao.update(entry);                        
        }
        else
        {
            LogEntry entry = new LogEntry(id, status, url);
            dao.insert(entry);
            downloadedShows.put(id, entry);
        }
    }
    
    public Date lastAttempt(Episode episode)
    {
        LogEntry entry = getEntry(episode);
        if (entry == null) { return new Date(0); }
        return entry.getDate();
    }
    
    public boolean isInvalid(Episode ep, String url)
    {
        if (!downloadedShows.containsKey(ep.getIdString())) { return false; }
        LogEntry entry = downloadedShows.get(ep.getIdString());
        return entry.getStatus() == Status.INVALID && entry.getUrl().equals(url);
    }
    
    private LogEntry getEntry(Episode episode)
    {
        return downloadedShows.get(episode.getIdString());
    }
 
    public List<LogEntry> getLatestCompleted()
    {
        return getLatestWithStatus(20, Status.COMPLETED);
    }
    
    public List<LogEntry> getLatestSuccess()
    {
        return getLatestWithStatus(20, Status.SUCCESS);
    }
    
    public List<LogEntry> getHardToFind()
    {
        return getLatestWithStatus(20, Status.NOTFOUND);
    }
    
    public List<LogEntry> getLatestWithStatus(int num, Status status)
    {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        for (LogEntry entry : downloadedShows.values())
        {
            if (entry.getStatus() == status)
            {
                entries.add(entry);
            }
        }
        
        Collections.sort(entries, LogEntry.BY_DATE);
        
        return new ArrayList<LogEntry>(entries.subList(0, Math.min(num, entries.size()))); 
    }
    
    public List<Episode> getNextEpisodes(TVShow show)
    {
        List<Episode> toDownload = new ArrayList<Episode>();
        for (Episode e : show.getEpisodes())
        {
            if (e.getSeason().getNumber() >= show.getStartSeason() && !alreadyDownloaded(e))
            {
                log.debug("episode: {} has not been downloaded", e.getIdString());
                toDownload.add(e);
            }
        }
        
        return toDownload;
    }
    
    public boolean alreadyDownloaded(Episode episode)
    {
        return downloadedShows.containsKey(episode.getIdString()) 
               && (downloadedShows.get(episode.getIdString()).getStatus() == Status.SUCCESS
               || downloadedShows.get(episode.getIdString()).getStatus() == Status.COMPLETED);
    }
    
    public int size()
    {
        return downloadedShows.size();
    }
    
    boolean contains(String showID)
    {
        return downloadedShows.containsKey(showID);
    }
}
