package com.jeffskj.torrent.config;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.jeffskj.torrent.showinfo.ShowInfoProvider;

public class ConfigurationProvider implements Provider<Configuration>
{
    private Logger log = LoggerFactory.getLogger(ConfigurationProvider.class);
    private ShowInfoProvider showProvider;
    
    
    @Inject
    public ConfigurationProvider(ShowInfoProvider showProvider)
    {
        this.showProvider = showProvider;
    }
    
    public Configuration get()
    {
        Configuration configuration = new ConfigurationManager().load();
        for (TVShow show : configuration.getShows())
        {
            updateShowInfo(show);
        }
        return configuration;
    }
    
    private void updateShowInfo(TVShow show) 
    {
        TVShow actualInfo = showProvider.getShowInfo(show.getName());
        try
        {
            int startSeason = show.getStartSeason();
            PropertyUtils.copyProperties(show, actualInfo);
            show.setStartSeason(startSeason);
        }
        catch (Exception e)
        {
            log.error("problem updating show info for show " + show.getName(), e);
            throw new RuntimeException("Error updating show info!");
        }
    }
}
