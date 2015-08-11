// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.discover.datainsight;

import java.net.MalformedURLException;
import java.net.URL;

public class SharePointURLParser
{
    private final URL url;
    private static final int REST_OF_URL_SPLIT_LIMIT = 2;
    private final String webApp;
    private final String[] pathElements;
    
    public SharePointURLParser(final String itemURL) throws MalformedURLException {
        this.url = new URL(itemURL);
        this.webApp = this.createWebApp(this.url);
        this.pathElements = this.createPathElements(this.url);
    }
    
    private String[] createPathElements(final URL url) {
        String restOfURL = url.getPath();
        if (restOfURL.startsWith("/")) {
            restOfURL = restOfURL.substring(1);
        }
        return restOfURL.split("/", 2);
    }
    
    private String createWebApp(final URL url) {
        final String protocol = url.getProtocol();
        final String authority = url.getAuthority();
        if (protocol != null && authority != null) {
            return protocol + "://" + authority;
        }
        throw new IllegalArgumentException("The URL is invalid: " + url.toString());
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public String getWebApp() {
        return this.webApp;
    }
    
    public String[] getPathElements() {
        return this.pathElements;
    }
}
