package com.jeffskj.torrent.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Maps;
import com.jeffskj.torrent.providers.TorrentProvider;

public class Configuration
{
    private ArrayList<TorrentProvider> providers = new ArrayList<TorrentProvider>();
    private List<TVShow> shows = new ArrayList<TVShow>();
    private File torrentDownloadLocation;
    private File finishedDownloadLocation;
    private File videoLibraryLocation;
    private String webUiHost;
    private int webUiPort;
    private String webUiUser;
    private String webUiPass;
    
    
    private Properties properties = new Properties();
    
    public void addProvider(TorrentProvider provider)
    {
        providers.add(provider);
    }

    public void addProvider(TorrentProvider provider, int index)
    {
        providers.add(index, provider);
    }
    
    public void setTorrentDownloadLocation(File downloadLocation)
    {
        torrentDownloadLocation = downloadLocation;
    }

    public void setFinishedDownloadLocation(File finishedDownloadLocation)
    {
        this.finishedDownloadLocation = finishedDownloadLocation;
    }

    public List<TorrentProvider> getTorrentProviders()
    {
        return new ArrayList<TorrentProvider>(providers);
    }

    public File getTorrentDownloadLocation()
    {
        return torrentDownloadLocation;
    }

    public File getFinishedDownloadLocation()
    {
        return finishedDownloadLocation;
    }

    public void addShow(TVShow show)
    {
        shows.add(show);
    }

    public List<TVShow> getShows()
    {
        return new ArrayList<TVShow>(shows);
    }
    
    public TVShow getShow(String name)
    {
        for (TVShow show : shows)
        {
            if (show.getName().equals(name)) 
            {
                return show;
            }
        }
        return null;
    }

    public void setVideoLibraryLocation(File videoLibraryLocation)
    {
        this.videoLibraryLocation = videoLibraryLocation;
    }

    public File getVideoLibraryLocation()
    {
        return videoLibraryLocation;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return Maps.fromProperties(properties);
    }

    public String getWebUiHost() {
        return webUiHost;
    }

    public void setWebUiHost(String webUiHost) {
        this.webUiHost = webUiHost;
    }

    public int getWebUiPort() {
        return webUiPort;
    }

    public void setWebUiPort(int webUiPort) {
        this.webUiPort = webUiPort;
    }

    public String getWebUiUser() {
        return webUiUser;
    }

    public void setWebUiUser(String webUiUser) {
        this.webUiUser = webUiUser;
    }

    public String getWebUiPass() {
        return webUiPass;
    }

    public void setWebUiPass(String webUiPass) {
        this.webUiPass = webUiPass;
    }
}
