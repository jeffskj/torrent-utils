package com.jeffskj.torrent.handler;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.Response;

public class FileDownloadTorrentHandler implements TorrentHandler {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloadTorrentHandler.class);

    private final HttpClient client;
    private final Configuration config;

    @Inject
    public FileDownloadTorrentHandler(HttpClient client, Configuration config) {
        this.client = client;
        this.config = config;
    }

    public void handle(Torrent torrent) {
        Response response = client.get(torrent.getUrl(), false);
        FileOutputStream output = null;

        try {
            File file = new File(config.getTorrentDownloadLocation(), response.getFilename());
            logger.info("writing torrent data to: {}", file);
            output = new FileOutputStream(file);
            IOUtils.copy(response.getBody(), output);
        } catch (Exception e) {
            throw new RuntimeException("error downloading torrent file", e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
