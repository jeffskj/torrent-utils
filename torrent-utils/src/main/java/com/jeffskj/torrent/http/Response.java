/**
 * 
 */
package com.jeffskj.torrent.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.jeffskj.torrent.StringUtils;

public class Response
{
    private static final Pattern FILENAME_PATTERN = Pattern.compile("filename=\"([^\"]+)\""); 
    private String body;
    private String filename;
    private final String charset;
    
    public Response(HttpResponse response, String uri)
    {
        try {
            charset = EntityUtils.getContentCharSet(response.getEntity());
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        filename = checkForFileName(response.getAllHeaders(), StringUtils.substringAfterLast(uri, "/"));
    }

    public Response(String body, String uri) {
        charset = Charsets.UTF_8.name();
        this.body = body;
        filename = StringUtils.substringAfterLast(uri, "/");
    }
    
    private String checkForFileName(Header[] headers, String defaultValue)
    {
        for (Header header : headers)
        {
            if (header.getName().equals("Content-Disposition"))
            {
                Matcher m = FILENAME_PATTERN.matcher(header.getValue());
                if (m.find())
                {
                    return m.group(1);
                }
            }
        }
        return defaultValue;
    }

    public InputStream getBody()
    {
        try {
            return IOUtils.toInputStream(body, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBodyAsString() {
        return body;
    }
    
    public String getFilename()
    {
        return filename;
    }
}