package com.jeffskj.torrent.handler;

import com.jeffskj.torrent.config.Torrent;

public interface TorrentHandler {
    void handle(Torrent torrent);
}
