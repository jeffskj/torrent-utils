package com.jeffskj.torrent;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.handler.TorrentHandler;
import com.jeffskj.torrent.log.DownloadLog;
import com.jeffskj.torrent.providers.TorrentProvider;

public class TorrentDownloaderTest extends EasyMockSupport {
    private static final String TORRENT_URL = "http://foo/test.torrent";

    @Test
    public void testDownloadTorrent() throws IOException {

        Configuration configuration = new Configuration();
        Episode s2e1 = new Episode(1);
        s2e1.setAirDate(DateUtils.addDays(new Date(), -1));
        TVShow show = new TVShow("House").addSeason(new Season(2).addEpisode(s2e1));
        show.setRuntime(60);

        Episode s1e1 = new Episode(1);
        s1e1.setAirDate(DateUtils.addDays(new Date(), -1));
        show.addSeason(new Season(1).addEpisode(s1e1));
        show.setStartSeason(2);

        configuration.addShow(show);

        TorrentProvider tp = createMock(TorrentProvider.class);
        Torrent torrent = new Torrent(TORRENT_URL, 350.0, 100, 0);
        
        Set<Torrent> torrents = Sets.newHashSet(torrent, new Torrent("FAKE", 1053.0, 100, 0), 
                new Torrent("BAD_RATIO", 350.0, 1, 100));
        
        expect(tp.getTorrents(configuration, s2e1)).andReturn(torrents);
        configuration.addProvider(tp, 0);

        DownloadLog log = createMock(DownloadLog.class);
        expect(log.isInvalid(s2e1, TORRENT_URL)).andReturn(false).anyTimes();
        expect(log.isInvalid(s2e1, "FAKE")).andReturn(true).anyTimes();
        expect(log.isInvalid(s2e1, "BAD_RATIO")).andReturn(false).anyTimes();
        
        expect(log.getNextEpisodes(show)).andReturn(Arrays.asList(s1e1, s2e1)).anyTimes();
        log.addDownload(s2e1, TORRENT_URL);

        TorrentHandler handler = createMock(TorrentHandler.class);
        handler.handle(torrent);

        replayAll();

        TorrentDownloader torrentDownloader = new TorrentDownloader(configuration, log, handler);
        torrentDownloader.downloadNextShows();

        verifyAll();
    }

    @Test
    public void shouldAttemptDownloads() {
        Date now = new Date();
        Date yesterday = DateUtils.addDays(now, -1);
        Date old = DateUtils.addDays(now, -100);

        DownloadLog log = EasyMock.createMock(DownloadLog.class);
        EasyMock.expect(log.lastAttempt((Episode) EasyMock.anyObject())).andReturn(now).anyTimes();
        EasyMock.replay(log);

        TorrentDownloader downloader = new TorrentDownloader(null, log, null);
        TVShow show = new TVShow("House");
        show.addSeason(new Season(1).addEpisode(new Episode(1, old)).addEpisode(new Episode(2, yesterday)));
        show.addSeason(new Season(2).addEpisode(new Episode(1, old)).addEpisode(new Episode(2, yesterday)));
        show.setStartSeason(2);

        assertFalse(downloader.shouldAttemptDownload(show.getSeason(1).getEpisode(1)));
        assertFalse(downloader.shouldAttemptDownload(show.getSeason(1).getEpisode(2)));
        assertFalse(downloader.shouldAttemptDownload(show.getSeason(2).getEpisode(1)));
        assertTrue(downloader.shouldAttemptDownload(show.getSeason(2).getEpisode(2)));

        log = EasyMock.createMock(DownloadLog.class);
        EasyMock.expect(log.lastAttempt((Episode) EasyMock.anyObject())).andReturn(old).anyTimes();
        EasyMock.replay(log);

        assertFalse(downloader.shouldAttemptDownload(show.getSeason(1).getEpisode(1)));
        assertFalse(downloader.shouldAttemptDownload(show.getSeason(1).getEpisode(2)));
        assertFalse(downloader.shouldAttemptDownload(show.getSeason(2).getEpisode(1)));
        assertTrue(downloader.shouldAttemptDownload(show.getSeason(2).getEpisode(2)));
    }

    // @Test
    // public void testDownload() {
    // Response response = new
    // SimpleHttpClient().get("http://torrents.thepiratebay.org/6875563/The_Big_Bang_Theory_S05E11_PROPER_HDTV_XviD-2HD_%5Beztv%5D.6875563.TPB.torrent");
    // Configuration config = new Configuration();
    // config.setTorrentDownloadLocation(new File("d:/crap"));
    // TorrentDownloader downloader = new TorrentDownloader(config, null, null);
    // downloader.writeTorrent("test.torrent", response.getBody());
    // }
}
