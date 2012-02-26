package com.jeffskj.torrent.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpClient implements com.jeffskj.torrent.http.HttpClient {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private DefaultHttpClient client = new DefaultHttpClient(new SingleClientConnManager());

    public Response get(String uri, boolean escape, String user, String pass) {
        URI parsedUri;
        try {
            parsedUri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        BasicHttpContext ctx = new BasicHttpContext();

        HttpGet get = new HttpGet();
        get.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.NETSCAPE);

        try {
            get.setURI(parsedUri);

            if (user != null && pass != null) {
                AuthCache authCache = new BasicAuthCache();
                BasicScheme scheme = new BasicScheme();
                
                authCache.put(new HttpHost(parsedUri.getHost()), scheme);
                ctx.setAttribute(ClientContext.AUTH_CACHE, authCache);
                
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));
                ctx.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);
            }

            HttpResponse response = client.execute(get, ctx);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.warn("response code was not 200, was: {}", response.getStatusLine());
                logger.debug("response body: {}", EntityUtils.toString(response.getEntity()));
            }
            
            return new Response(response, uri);
        } catch (IOException e) {
            logger.warn("error getting response from uri " + uri, e);
            return null;
        }
    }

    public Response get(String uri, boolean escape) {
        return get(uri, escape, null, null);
    }

    public Response get(String uri) {
        return get(uri, true);
    }

}
