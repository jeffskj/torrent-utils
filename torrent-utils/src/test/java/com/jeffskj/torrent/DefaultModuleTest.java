package com.jeffskj.torrent;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class DefaultModuleTest
{
    @Test
    @Ignore
    public void testGuiceInitializes()
    {
        Injector injector = Guice.createInjector(new DefaultModule());
        injector.getInstance(TorrentDownloader.class);
        injector.getInstance(TorrentOrganizer.class);
    }
    
}
