package com.jeffskj.torrent.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;

public class ResponseTest extends EasyMockSupport {
    @Test
    public void getFilename() throws IllegalStateException, IOException {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Content-Disposition", "torrent/file filename=\"foo.torrent\" ");
        HttpResponse response = createMock(HttpResponse.class);
        EasyMock.expect(response.getAllHeaders()).andReturn(headers);
        EasyMock.expect(response.getEntity()).andReturn(new ByteArrayEntity("test".getBytes())).anyTimes();
        replayAll();
        
        Response r = new Response(response, "http://foo/get/");
        assertEquals("foo.torrent", r.getFilename());
    }

    
    @Test
    public void getFilenameNoHeaders() throws IllegalStateException, IOException {
        HttpResponse response = createMock(HttpResponse.class);
        EasyMock.expect(response.getAllHeaders()).andReturn(new Header[0]);
        EasyMock.expect(response.getEntity()).andReturn(new ByteArrayEntity("test".getBytes())).anyTimes();
        replayAll();

        Response r = new Response(response, "http://foo/get/foo.torrent");
        assertEquals("foo.torrent", r.getFilename());
    }

}
