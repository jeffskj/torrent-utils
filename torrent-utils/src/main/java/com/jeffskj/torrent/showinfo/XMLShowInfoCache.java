package com.jeffskj.torrent.showinfo;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeffskj.torrent.ResourceLocator;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.thoughtworks.xstream.XStream;

public class XMLShowInfoCache implements ShowInfoCache
{
    private static Logger log = LoggerFactory.getLogger(XMLShowInfoCache.class);
    static final String CACHE_DIR = "show-cache/";
    
    private HashMap<String, CachedTVShow> cache = new HashMap<String, CachedTVShow>();
    private XStream xstream = new XStream();
    private File cacheDir; 
    
    public XMLShowInfoCache()
    {
        this(ResourceLocator.locate(CACHE_DIR));
    }

    public XMLShowInfoCache(File cacheDir)
    {
        xstream.alias("show", CachedTVShow.class);
        xstream.alias("season", Season.class);
        xstream.alias("episode", Episode.class);
        this.cacheDir = cacheDir;
        if (!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }
        readCache();        
    }
    
    public void eject(TVShow show)
    {
        cache.remove(show.getName());
        removeFromCache(show);
    }
    
    private void removeFromCache(TVShow show)
    {
        getFile(show).delete();
    }

    private void readCache()
    {
        log.info("reading cache from {}", cacheDir);
        ObjectInputStream stream = null;
        for (File f : cacheDir.listFiles())
        {
            if (!f.getName().endsWith(".xml")) { continue; }
            try
            {
                    stream = xstream.createObjectInputStream(new FileInputStream(f));
                    while (true)
                    {
                        CachedTVShow show = (CachedTVShow) stream.readObject();
                        cache.put(show.show.getName(), show);
                    }
            }
            catch (EOFException e) {/*eof, not an exception actually*/}
            catch (Exception e)
            {
                log.warn("error reading from cache file {}", f);
                log.warn("", e);
            }
            finally
            {
                try
                {
                    if (stream != null)
                    {
                        stream.close();
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    private File getFile(TVShow show)
    {
        return new File(cacheDir, show.getName() + ".xml");
    }

    private void writeCache(CachedTVShow show)
    {
        ObjectOutputStream stream = null;
        try
        {
            File f = getFile(show.show);
            stream = xstream.createObjectOutputStream(new FileOutputStream(f));
            stream.writeObject(show);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public TVShow get(String name)
    {   
        return cache.get(name) != null ? cache.get(name).show : null;
    }
    
    public Date getCachedDate(TVShow show)
    {
        return cache.get(show.getName()) != null ? cache.get(show.getName()).cached : null;
    }
    
    public void put(TVShow show)
    {
        CachedTVShow cachedShow = new CachedTVShow(show, new Date());
        cache.put(show.getName(), cachedShow);
        writeCache(cachedShow);
    }
    
    static class CachedTVShow 
    {
        public TVShow show;
        public Date cached;
        
        public CachedTVShow(TVShow show, Date cached)
        {
            this.show = show;
            this.cached = cached;
        }
    }
}
