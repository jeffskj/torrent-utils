package com.jeffskj.torrent.config;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Episode 
{
    private static final Pattern ID_STRING_PATTERN = Pattern.compile("(.*) S(\\d{2})E(\\d{2})");
    private int episodeNum;
    private int absoluteEpisodeNum;
    private String productionNum;
    private Date airDate;
    private String detailsLink;
    private String title;
    private Season season;

    public Episode(int num, Date airDate)
    {
        episodeNum = num;
        this.airDate = airDate;
    }
    
    public Episode(int num)
    {
        this(num, null);
    }
    
    public Episode (String idString)
    {
        Matcher matcher = ID_STRING_PATTERN.matcher(idString);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException("Invalid episode identifier");
        }
        TVShow show = new TVShow(matcher.group(1));
        season = new Season(Integer.parseInt(matcher.group(2)));
        show.addSeason(season);
        
        episodeNum = Integer.parseInt(matcher.group(3));
        season.addEpisode(this);
    }
    
    public Episode()
    {
    }
    
    public int getEpisodeNum()
    {
        return episodeNum;
    }

    public void setEpisodeNum(int episodeNum)
    {
        this.episodeNum = episodeNum;
    }

    public int getAbsoluteEpisodeNum()
    {
        return absoluteEpisodeNum;
    }

    public void setAbsoluteEpisodeNum(int absoluteEpisodeNum)
    {
        this.absoluteEpisodeNum = absoluteEpisodeNum;
    }

    public String getProductionNum()
    {
        return productionNum;
    }

    public void setProductionNum(String productionNum)
    {
        this.productionNum = productionNum;
    }

    public Date getAirDate()
    {
        return airDate;
    }

    public void setAirDate(Date airDate)
    {
        this.airDate = airDate;
    }

    public String getDetailsLink()
    {
        return detailsLink;
    }

    public void setDetailsLink(String detailsLink)
    {
        this.detailsLink = detailsLink;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setSeason(Season season)
    {
        this.season = season;
    }

    public Season getSeason()
    {
        return season;
    }
    
    @Override
    public String toString()
    {
        return getIdString();
    }

    public TVShow getShow()
    {
        if (getSeason() == null) { return null; }
        return getSeason().getShow();
    }

    public String getFileName()
    {
        return sanitizeForFS(String.format("%s - S%02dE%02d - %s", getShowName(), season.getNumber(), 
            episodeNum, StringUtils.defaultIfEmpty(title, "Untitled")));
    }
    
    private String sanitizeForFS(String filename)
    {
        return StringUtils.replaceChars(filename, "<>:\"/\\|?*", "");
    }


    public String getPath()
    {
        return String.format("%s/Season %02d/%s", getShowName(), season.getNumber(), getFileName());
    }
    
    public String getShowName()
    {
        return getShow().getName();
    }
    
    public String getIdString()
    {
        return String.format("%s S%02dE%02d", getShowName(), season.getNumber(), episodeNum);
    }
    
    public boolean matches(String input) 
    {
        //get rid of region specific identifier
        String sanitizedShowName = sanitize(getShowName().replace("(US)", ""));
        String sanitizedTitle = sanitize(title);
        
        String idFormats = getIDFormats("s%02de%02d", "%02dx%02d", "%dx%02d", "%dx%d", "%d%02d", 
                "season%depisode%d", "season%depisode%02d", "season%02depisode%02d");
        
        Pattern pattern = Pattern.compile(sanitizedShowName + "(?:us)?" + idFormats + "(?!\\d)");
        
        String sanitizedInput = sanitize(input);
        sanitizedInput = sanitizedInput.replace(sanitizedTitle, "");
        return pattern.matcher(sanitizedInput).find();
    }
    
    private String getIDFormats(String... patterns)
    {
        StringBuilder sb = new StringBuilder().append("(?:");
        
        for (String pattern : patterns)
        {
            sb.append(String.format(pattern, getSeason().getNumber(), getEpisodeNum())).append('|');
        }
        
        return sb.substring(0, sb.length()-1) + ')';
    }
    
    private String sanitize(String str) 
    {
        if (str == null) { return ""; }
        return str.toLowerCase().replaceAll("[^0-9a-z]+", "");
    }
}
