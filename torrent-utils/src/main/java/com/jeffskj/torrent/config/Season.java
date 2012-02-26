package com.jeffskj.torrent.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jeffskj.torrent.StringUtils;

public class Season
{
    private List<Episode> episodes = new ArrayList<Episode>();
    private int number;
    private TVShow show;
    
    public Season(int num)
    {
        number = num;
    }
    
    public Season()
    {
    }
    
    public List<Episode> getEpisodes()
    {
        return episodes;
    }
    
    public Episode getEpisode(int num)
    {
        for (Episode e : episodes)
        {
            if (e.getEpisodeNum() == num)
            {
                return e;
            }
        }
        
        return null;
    }
    
    public Season addEpisode(Episode episode)
    {
        episode.setSeason(this);
        episodes.add(episode);
        return this;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return number;
    }
    
    public String getFolderName()
    {
        return "Season " + StringUtils.twoDigits(number);
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public void setShow(TVShow show)
    {
        this.show = show;
    }

    public TVShow getShow()
    {
        return show;
    }
}
