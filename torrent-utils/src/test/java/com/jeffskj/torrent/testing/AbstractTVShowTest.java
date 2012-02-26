package com.jeffskj.torrent.testing;

import java.io.InputStream;

import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;

public class AbstractTVShowTest
{
    public Episode getEpisode(String showName, int season, int num) 
    {
        return getEpisode(showName, season, num, "");
    }
    
    public Episode getEpisode(String showName, int season, int num, String title) 
    {
        Episode episode = new Episode(num);
        episode.setTitle(title);
        new TVShow(showName).addSeason(new Season(season).addEpisode(episode));
        return episode;
    }
    
    public InputStream getTestResource(String name)
    {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}
