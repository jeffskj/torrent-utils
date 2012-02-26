package com.jeffskj.torrent.tasks;

import com.google.inject.Inject;
import com.jeffskj.torrent.WhatsNewOrganizer;

public class WhatsNewOrganizerTask implements Runnable
{
    private final WhatsNewOrganizer organizer;

    @Inject
    public WhatsNewOrganizerTask(WhatsNewOrganizer organizer)
    {
        this.organizer = organizer;
    }
    
    public void run()
    {
        organizer.copyNewDownloads();
        organizer.deleteOldDownloads();
    }
}
