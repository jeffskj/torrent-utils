package com.jeffskj.torrent.tasks;

import com.google.inject.Inject;
import com.jeffskj.torrent.TorrentOrganizer;

public class TorrentOrganizerTask implements Runnable
{
    private final TorrentOrganizer organizer;

    @Inject
    public TorrentOrganizerTask(TorrentOrganizer organizer)
    {
        this.organizer = organizer;
    }
    
    public void run()
    {
        organizer.organizeDownloads();
        organizer.organizeLibrary();
    }
}
