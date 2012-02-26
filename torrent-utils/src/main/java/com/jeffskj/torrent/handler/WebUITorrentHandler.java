package com.jeffskj.torrent.handler;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.jeffskj.torrent.UrlUtils;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;

public class WebUITorrentHandler implements TorrentHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebUITorrentHandler.class);

    private final HttpClient client;
    private final Configuration config;

    @Inject
    public WebUITorrentHandler(HttpClient client, Configuration config) {
        this.client = client;
        this.config = config;
    }

    public void handle(Torrent torrent) {
        logger.info("adding torrent url {}", torrent.getUrl());
        String url = getUrl(torrent.getUrl());
        logger.debug("calling uTorrent webui with url {}", url);
        client.get(url, false, config.getWebUiUser(), config.getWebUiPass());
    }
    
    private String getUrl(String torrentUrl) {
        return StrSubstitutor.replace("http://${host}:${port}/gui/?action=add-url&s=${url}", ImmutableMap.of(
                "host", config.getWebUiHost(),
                "port", config.getWebUiPort(),
                "url", UrlUtils.encode(torrentUrl)));
    }
}
