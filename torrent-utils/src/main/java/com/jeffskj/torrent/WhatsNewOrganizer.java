package com.jeffskj.torrent;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.log.DownloadLog;
import com.jeffskj.torrent.log.LogEntry;

public class WhatsNewOrganizer
{
    private static String NEW_DOWNLOADS_FOLDER = "TV Shows/What's New";
    private Logger log = LoggerFactory.getLogger(getClass());
    private final Configuration config;
    private final DownloadLog downloads;
    private final File newDownloadsFolder;
    
    @Inject
    public WhatsNewOrganizer(Configuration config, DownloadLog downloads)
    {
        this.config = config;
        this.downloads = downloads;
        newDownloadsFolder = new File(config.getVideoLibraryLocation(), NEW_DOWNLOADS_FOLDER);
    }

    public void copyNewDownloads()
    {
        Date cutoff = DateUtils.addWeeks(new Date(), -2);
        
        List<LogEntry> recentDownloads = downloads.getLatestCompleted().subList(0, 10);
        log.info("recent downloads {}", recentDownloads);
        
        for (LogEntry entry : recentDownloads)
        {
            // parse the ID string, but we still need to get the actual show data
            Episode episode = new Episode(entry.getShowId());
            episode = config.getShow(episode.getShowName()).getSeason(episode.getSeason().getNumber()).getEpisode(episode.getEpisodeNum());
            
            String childPath = "TV Shows/" + episode.getPath();
            File showFolder = new File(config.getVideoLibraryLocation(), childPath).getParentFile();
            log.debug("looking in show folder {} for files with prefix {}", showFolder, episode.getFileName());
            File[] matches = showFolder.listFiles((FilenameFilter)new PrefixFileFilter(episode.getFileName()));
            
            log.debug("found {} matches", matches.length);
            if (matches.length == 1)
            {
                File showFile = matches[0];
                if (new Date(showFile.lastModified()).before(cutoff)) { continue; }
                
                try
                {
                    File destFile = new File(newDownloadsFolder, showFile.getName());
                    if (!destFile.exists())
                    {
                        log.info("copying {} to {}", showFile, destFile);
                        FileUtils.copyFile(showFile, destFile);
                    }
                }
                catch (IOException e)
                {
                    log.error("problem copying to new downloads folder!", e);
                }
            }
        }
    }

    public void deleteOldDownloads()
    {
        Date cutoff = DateUtils.addWeeks(new Date(), -2);
        File[] files = newDownloadsFolder.listFiles();
        for (File f : files) 
        {
            if (new Date(f.lastModified()).after(cutoff)) { continue; }
            
            log.info("deleting {}", f);
            if (!f.delete())
            {
                log.warn("could not delete file {}", f);
            }
        }
    }
}
