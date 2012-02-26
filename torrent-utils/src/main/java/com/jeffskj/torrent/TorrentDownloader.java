package com.jeffskj.torrent;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.handler.TorrentHandler;
import com.jeffskj.torrent.log.DownloadLog;
import com.jeffskj.torrent.providers.TorrentProvider;

public class TorrentDownloader {
    private static final int MIN_SIZE_MULTIPLIER = 5;
    private static final int MAX_SIZE_MULTIPLIER = 15;
    private static final Date YESTERDAY = DateUtils.addDays(new Date(), -1);
    private static final Date OLD_SHOW_CUTOFF = DateUtils.addDays(new Date(), -90);

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DownloadLog log;
    private Configuration config;
    private TorrentHandler handler;

    private static final long TIME_BETWEEN_DOWNLOADS = 1000;

    @Inject
    public TorrentDownloader(Configuration config, DownloadLog log, TorrentHandler handler) {
        this.config = config;
        this.log = log;
        this.handler = handler;
    }

    public void downloadNextShows() {
        logger.info("downloading next episodes from configured TV shows.");
        for (TVShow show : config.getShows()) {
            for (Episode next : log.getNextEpisodes(show)) {
                if (shouldAttemptDownload(next)) {
                    try {
                        downloadTorrent(next);
                    } catch (RuntimeException e) {
                        logger.warn("error downloading episode:", e);
                        log.addFailure(next);
                    }
                    sleepUninterruptibly(TIME_BETWEEN_DOWNLOADS, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public boolean shouldAttemptDownload(Episode episode) {
        if (episode.getSeason().getNumber() < episode.getShow().getStartSeason()) {
            return false;
        }

        Date airDate = episode.getAirDate();
        if (airDate.before(OLD_SHOW_CUTOFF)) {
            return log.lastAttempt(episode).before(YESTERDAY);
        }

        return airDate.before(new Date());
    }

    public void downloadTorrent(Episode episode) {
        logger.info("attempting to download {}", episode.getIdString());
        boolean downloaded = false;
        for (TorrentProvider site : config.getTorrentProviders()) {
            if (downloadTorrentFile(site, episode)) {
                downloaded = true;
                break;
            }
        }

        if (!downloaded) {
            log.addAttempt(episode);
        }

    }

    private boolean downloadTorrentFile(TorrentProvider site, final Episode episode) {
        Set<Torrent> torrents = site.getTorrents(config, episode);
        logger.debug("found {} matching torrents", torrents.size());
        
        torrents = Sets.filter(torrents, new Predicate<Torrent>() {
            public boolean apply(Torrent t) {
                int runtime = episode.getShow().getRuntime();
                return !log.isInvalid(episode, t.getUrl()) && (t.getSize() == -1.0 || 
                        (t.getSize() < runtime * MAX_SIZE_MULTIPLIER && t.getSize() > runtime * MIN_SIZE_MULTIPLIER)); 
            }
        });
        
        if (torrents.isEmpty()) {
            return false;
        }
        
        Torrent torrent = Collections.min(torrents);
        log.addDownload(episode, torrent.getUrl());
        handler.handle(torrent);

        return true;
    }
}