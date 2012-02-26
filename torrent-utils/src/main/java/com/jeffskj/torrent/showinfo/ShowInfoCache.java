package com.jeffskj.torrent.showinfo;

import java.util.Date;

import com.jeffskj.torrent.config.TVShow;

public interface ShowInfoCache
{

    public abstract void eject(TVShow show);

    public abstract TVShow get(String name);

    public abstract Date getCachedDate(TVShow show);

    public abstract void put(TVShow show);

}