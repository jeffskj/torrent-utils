package com.jeffskj.torrent.config;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Torrent implements Comparable<Torrent>
{
    private String url;
    private int seeds;
    private int leeches;
    private double size; //size in megabytes
    
    public Torrent(String url, double size, int seeds, int leeches)
    {
        this.url = url;
        this.seeds = seeds;
        this.leeches = leeches;
        this.size = size;
    }

    public Torrent()
    {
    }
    
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getSeeds()
    {
        return seeds;
    }

    public void setSeeds(int seeds)
    {
        this.seeds = seeds;
    }

    public int getLeeches()
    {
        return leeches;
    }

    public void setLeeches(int leeches)
    {
        this.leeches = leeches;
    }

    public double getSize()
    {
        return size;
    }

    public void setSize(double size)
    {
        this.size = size;
    }

    public double getSeedRatio()
    {
        if (leeches == 0) { return 1.0; }
        return seeds / leeches;
    }
    
    public int compareTo(Torrent other)
    {
        return ObjectUtils.compare(other.getSeedRatio() * other.seeds, getSeedRatio() * seeds);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + leeches;
        result = prime * result + seeds;
        long temp;
        temp = Double.doubleToLongBits(size);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Torrent other = (Torrent) obj;
        if (leeches != other.leeches)
            return false;
        if (seeds != other.seeds)
            return false;
        if (Double.doubleToLongBits(size) != Double.doubleToLongBits(other.size))
            return false;
        if (url == null)
        {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        return true;
    }
}
