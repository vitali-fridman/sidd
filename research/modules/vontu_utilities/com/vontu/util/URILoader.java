// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class URILoader
{
    public static InputStream load(final URI uri) throws IOException, FileNotFoundException {
        final String scheme = uri.getScheme();
        InputStream input;
        if (scheme.equalsIgnoreCase("file")) {
            input = loadFile(uri);
        }
        else if (scheme.equalsIgnoreCase("resource")) {
            input = loadJavaResource(uri);
        }
        else {
            if (!scheme.equalsIgnoreCase("http")) {
                throw new IOException("unsupported scheme: " + scheme);
            }
            input = loadHTTPResource(uri);
        }
        return input;
    }
    
    private static InputStream loadFile(final URI uri) throws FileNotFoundException {
        final String schemePart = uri.getSchemeSpecificPart();
        URI absoluteURI;
        if (schemePart.startsWith("///")) {
            absoluteURI = uri;
        }
        else if (schemePart.startsWith("//")) {
            final File absolutePath = new File(new File(System.getProperty("user.dir")), schemePart.substring(2));
            absoluteURI = absolutePath.toURI();
        }
        else {
            absoluteURI = uri;
        }
        final FileInputStream input = new FileInputStream(new File(absoluteURI));
        return input;
    }
    
    private static InputStream loadHTTPResource(final URI uri) throws IOException {
        try {
            final HttpURLConnection connection = (HttpURLConnection)uri.toURL().openConnection();
            return connection.getInputStream();
        }
        catch (MalformedURLException malformedURLException) {
            throw new IOException(malformedURLException.getMessage());
        }
    }
    
    private static InputStream loadJavaResource(final URI uri) throws FileNotFoundException {
        String resource = uri.getSchemeSpecificPart();
        if (resource.charAt(0) == '/') {
            resource = resource.substring(1);
        }
        final InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (input != null) {
            return input;
        }
        throw new FileNotFoundException(uri.toString());
    }
}
