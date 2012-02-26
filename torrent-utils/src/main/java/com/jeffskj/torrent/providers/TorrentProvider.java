package com.jeffskj.torrent.providers;

import java.util.Set;

import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Torrent;

public interface TorrentProvider
{
    Set<Torrent> getTorrents(Configuration config, Episode episode);
}