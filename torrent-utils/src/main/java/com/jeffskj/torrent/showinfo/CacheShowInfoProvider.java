package com.jeffskj.torrent.showinfo;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.TVShow;

public class CacheShowInfoProvider implements ShowInfoProvider
{
    public static final Date CACHE_AUTO_EXPIRE = DateUtils.addDays(new Date(), -1);
    private Logger log = LoggerFactory.getLogger(getClass());
    private final ShowInfoProvider cacheMissProvider;
    private ShowInfoCache cache;
    
    @Inject
    public CacheShowInfoProvider(@CacheMiss ShowInfoProvider cacheMissProvider, ShowInfoCache cache)
    {
        this.cacheMissProvider = cacheMissProvider;
        this.cache = cache;
    }
    
    public TVShow getShowInfo(String name)
    {
        log.debug("getting show info for {}", name);
        TVShow show = cache.get(name);
        if (show != null && cache.getCachedDate(show).before(CACHE_AUTO_EXPIRE) || show == null)
        {
            log.info("show info is not cached, getting from cache-miss provider");
            try
            {
                TVShow uncached = cacheMissProvider.getShowInfo(name);
                if (uncached != null)
                {
                    cache.put(uncached);
                    return uncached;
                }
            }
            catch (Exception e)
            {
                return show;
            }
        }
        
        return show;
    }

}
