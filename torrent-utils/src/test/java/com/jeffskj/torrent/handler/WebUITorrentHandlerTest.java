package com.jeffskj.torrent.handler;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.Torrent;
import com.jeffskj.torrent.http.HttpClient;
import com.jeffskj.torrent.http.SimpleHttpClient;

public class WebUITorrentHandlerTest extends EasyMockSupport {

    @Test
    public void testHandle() {
        HttpClient client = createMock(HttpClient.class);
        EasyMock.expect(client.get("http://localhost:80/gui/?action=add-url&s=TORRENT_URL", false, "admin", "admin")).andReturn(null);
        
        Configuration config = new Configuration();
        config.setWebUiHost("localhost");
        config.setWebUiPort(80);
        config.setWebUiUser("admin");
        config.setWebUiPass("admin");
        
        replayAll();
        WebUITorrentHandler handler = new WebUITorrentHandler(client, config);
        handler.handle(new Torrent("TORRENT_URL", 0, 0, 0));
        verifyAll();
    }

    @Test
    @Ignore
    public void integrationTestHandle() {
        Configuration config = new Configuration();
        config.setWebUiHost("localhost");
        config.setWebUiPort(15338);
        config.setWebUiUser("admin");
        config.setWebUiPass("admin");
        
        WebUITorrentHandler handler = new WebUITorrentHandler(new SimpleHttpClient(), config);
        handler.handle(new Torrent("magnet:?xt=urn:btih:acf6095eb2b6f975fcd1fded2fe0be65ffd8ce35&dn=House+S08E04+HDTV+XviD-LOL+%5Beztv%5D&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.ccc.de%3A80", 0, 0, 0));
    }

}
