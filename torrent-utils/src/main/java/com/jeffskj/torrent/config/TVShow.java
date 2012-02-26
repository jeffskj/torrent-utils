package com.jeffskj.torrent.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;


public class TVShow
{
    private String name;
    private int id;
    private String country;
    private Date started;
    private int runtime;
    private String status;
    private List<Season> seasons = new ArrayList<Season>();
    private int startSeason = 1;
    
    public TVShow(String name)
    {
        this.name = name;
    }
    
    public TVShow() { }
    
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public Date getStarted()
    {
        return started;
    }

    public void setStarted(Date started)
    {
        this.started = started;
    }

    public void setStartedDate(String date)
    {
        try
        {
            setStarted(DateUtils.parseDate(date, new String[]{"MMM/dd/yyyy"}));
        }
        catch (ParseException e) {/* if it doesn't parse, just don't set it */}
    }
    
    public int getRuntime()
    {
        return runtime;
    }

    public void setRuntime(int runtime)
    {
        this.runtime = runtime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setSeasons(List<Season> seasons)
    {
        this.seasons = seasons;
    }

    
    
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getTotalSeasons()
    {
        return seasons.size();
    }

    public TVShow addSeason(Season season)
    {
        if (season.getNumber() > 0)
        {
            season.setShow(this);
            seasons.add(season);
        }
        return this;
    }

    public List<Season> getSeasons()
    {
        return seasons;
    }
    
    public int getStartSeason()
    {
        return startSeason;
    }
    
    public Season getSeason(int num)
    {
        for (Season s : seasons)
        {
            if (s.getNumber() == num)
            {
                return s;
            }
        }
        
        return null;
    }
    
    public List<Episode> getEpisodes()
    {
        List<Episode> eps = new ArrayList<Episode>();
        
        for (Season s : seasons)
        {
            eps.addAll(s.getEpisodes());
        }
        
        return eps;
    }
    
    @Override
    public String toString() 
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    };
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) { return false; }
        return name.equals(((TVShow) obj).getName());
    }
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public void setStartSeason(int startSeason)
    {
        this.startSeason = startSeason;
    }
}
