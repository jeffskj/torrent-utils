package com.jeffskj.torrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.log.DownloadLog;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

public class TorrentOrganizer 
{
    private static final String UNSORTED_DIRECTORY = "Unsorted/";
    private Logger log = LoggerFactory.getLogger(getClass());
    private final Configuration config;
    private final DownloadLog downloads;

    @Inject
    public TorrentOrganizer(Configuration config, DownloadLog downloads)
    {
        this.config = config;
        this.downloads = downloads;
    }
    
    public void organizeDownloads()
    {
        log.info("organizing finished downloads folder");
        organizeFolder(config.getFinishedDownloadLocation(), config.getVideoLibraryLocation());
        log.info("finished organizing finished downloads folder");
    }

    /**
     * organizes the unsorted items in the library
     */
    public void organizeLibrary()
    {
        log.info("organizing library folder");
        organizeFolder(new File(config.getVideoLibraryLocation(), UNSORTED_DIRECTORY), config.getVideoLibraryLocation());
        log.info("finsihed organizing library folder");
    }
    
    @SuppressWarnings("unchecked")
    public void organizeFolder(File sourceRoot, File destinationRoot)
    {
        deleteMarkedFiles(sourceRoot);
        extractRarFiles(sourceRoot);
        Collection<File> files = FileUtils.listFiles(sourceRoot, new WildcardFileFilter("*.avi"), TrueFileFilter.INSTANCE);
        log.debug("found {} files to organize", files.size());
        
        for (File file : files)
        {
            File copyMarkerFile = new File(file.getAbsolutePath() + ".copied");
            if (copyMarkerFile.exists())
            {
                deleteCopiedFile(file, copyMarkerFile);
            }
            else
            {
                Episode episode = findMatchingEpisode(file);
                if (episode != null)
                {
                    copyMatchingEpisode(file, episode);
                }
                else
                {
                    copyFile(file, new File(config.getVideoLibraryLocation(), UNSORTED_DIRECTORY + file.getName()));
                }
            }
        }
    }

    private void copyMatchingEpisode(File file, Episode episode)
    {
        Matcher matcher = Pattern.compile("(xvid|divx)", Pattern.CASE_INSENSITIVE).matcher(file.getName());
        String format = "Xvid";
        if (matcher.find())
        {
            format = matcher.group(1);
        }
        
        String childPath = "TV Shows/" + episode.getPath() + " - " + format + ".avi";
        File destination = new File(config.getVideoLibraryLocation(), childPath);
        
        copyFile(file, destination);
        
        downloads.setCompleted(episode);
    }

    private void copyFile(File file, File destination)
    {
        if (notInSameDirectory(file, destination))
        {
            log.info("moving {} to {}", file, destination);
            try
            {
                FileUtils.forceMkdir(destination.getParentFile());
                FileUtils.copyFile(file, destination);
                
                File copyMarkerFile = new File(file.getAbsolutePath() + ".copied");
                FileUtils.writeStringToFile(copyMarkerFile, String.valueOf(System.currentTimeMillis()));
            }
            catch (IOException e)
            {
                log.warn("error copying file", e);
            }
        }
    }

    private void deleteCopiedFile(File file, File copyMarkerFile)
    {
        log.info("found file {} that has been copied to library, but has not been deleted yet.", file);
        try
        {
            Date copied = new Date(Long.parseLong(FileUtils.readFileToString(copyMarkerFile)));
            if (copied.before(DateUtils.addHours(new Date(), -1)))
            {
                FileUtils.forceDelete(file);
                FileUtils.forceDelete(copyMarkerFile);
            }
        }
        catch (Exception e)
        {
            log.warn("error deleting file {}", file);
        }
    }

    private Episode findMatchingEpisode(File file)
    {
        for (TVShow show : config.getShows())
        {
            for (Episode episode : show.getEpisodes())
            {
                if (episode.matches(file.getName()))
                {
                    return episode;
                }
            }
        }
        return null;
    }
    
    private boolean notInSameDirectory(File file, File destination)
    {
        return !destination.getParentFile().equals(file.getParentFile());
    }
   
    @SuppressWarnings("unchecked")
    public void deleteMarkedFiles(File folder)
    {
        Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter("*.delete"), TrueFileFilter.INSTANCE);
        for (File file : files)
        {
            log.info("found file {} to delete", file);
            FileUtils.deleteQuietly(new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-7)));
            file.delete();
            log.info("delete {} successfully", file);
        }
    }

    @SuppressWarnings("unchecked")
    public void extractRarFiles(File folder)
    {
        Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter("*.rar"), TrueFileFilter.INSTANCE);
        
        for (File file : files)
        {
            Archive rar = null;
            try
            {
                log.info("extracting rar file: {}", file);
                rar = new Archive(file);
                FileHeader fh = rar.nextFileHeader();
                FileOutputStream out = new FileOutputStream(file.getParentFile().getParent() + "/" + fh.getFileNameString());
                rar.extractFile(fh, out);
                out.close();
                log.info("finished extracting, deleting original rar");
                FileUtils.touch(new File(file.getParentFile().getAbsolutePath() + ".delete"));
                FileUtils.forceDelete(file.getParentFile());
            }
            catch (RarException e)
            {
                log.warn("error attempting to extract rar", e);
                
            }
            catch (IOException e)
            {
                
                log.warn("error attempting to extract rar and delete it.", e);
            }
            finally
            {
                try
                {
                    rar.close();
                }
                catch (IOException e)
                {
                    log.warn("error attempting to close rar", e);
                }
            }
        }
    }
}