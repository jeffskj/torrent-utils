package com.jeffskj.torrent.showinfo;

import com.jeffskj.torrent.config.TVShow;

public interface ShowInfoProvider
{
    public TVShow getShowInfo(String name);
}
