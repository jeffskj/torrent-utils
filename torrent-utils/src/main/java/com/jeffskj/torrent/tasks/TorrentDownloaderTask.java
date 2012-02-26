package com.jeffskj.torrent.tasks;

import com.google.inject.Inject;
import com.jeffskj.torrent.TorrentDownloader;

public class TorrentDownloaderTask implements Runnable
{
    private final TorrentDownloader downloader;

    @Inject
    public TorrentDownloaderTask(TorrentDownloader downloader)
    {
        this.downloader = downloader;
    }
    
    public void run()
    {
        downloader.downloadNextShows();
    }
}
