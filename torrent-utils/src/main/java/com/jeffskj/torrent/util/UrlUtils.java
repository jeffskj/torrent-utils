package com.jeffskj.torrent.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.common.base.Charsets;

public class UrlUtils {
    public static String encode(String s) {
        try {
            return URLEncoder.encode(s, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
