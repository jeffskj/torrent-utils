package com.jeffskj.torrent;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.http.Response;
import com.jeffskj.torrent.http.SimpleHttpClient;

public class SimpleHttpClientTest {
    @Test
    @Ignore
    public void getReponseWithFilename() throws IOException {
        SimpleHttpClient client = new SimpleHttpClient();
        Response response = client.get("http://www.mininova.org/get/2362270");
        try {
            assertEquals("The.Big.Bang.Theory.S02E17.HDTV.XviD-XOR [mininova].torrent", response.getFilename());
        } finally {
            response.getBody().close();
        }

        response = client
                .get("http://torrents.thepiratebay.org/4696053/The_Office_S05E13_PROPER_HDTV_XviD-2HD-NoRar______.4696053.TPB.torrent");
        try {
            assertEquals("The_Office_S05E13_PROPER_HDTV_XviD-2HD-NoRar______.4696053.TPB.torrent",
                    response.getFilename());
        } finally {
            response.getBody().close();
        }
    }

}
