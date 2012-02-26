package com.jeffskj.torrent.http;

import com.google.inject.ImplementedBy;

@ImplementedBy(SimpleHttpClient.class)
public interface HttpClient
{
    Response get(String uri, boolean escape, String user, String pass);
    Response get(String uri, boolean escape);
    Response get(String uri);
}