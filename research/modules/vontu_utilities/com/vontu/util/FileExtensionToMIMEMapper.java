// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.InputStream;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;

public class FileExtensionToMIMEMapper
{
    private static final String DEFAULT_FILE_LOCATION = "resource:/com/vontu/util/mime.types";
    private Hashtable _table;
    
    public FileExtensionToMIMEMapper() throws IOException, URISyntaxException {
        this._table = new Hashtable();
        final URI uri = new URI("resource:/com/vontu/util/mime.types");
        this.init(uri);
    }
    
    public FileExtensionToMIMEMapper(final URI uri) throws IOException {
        this._table = new Hashtable();
        this.init(uri);
    }
    
    private void init(final URI uri) throws IOException {
        final InputStream input = URILoader.load(uri);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (reader.ready()) {
            final String line = reader.readLine();
            final StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.countTokens() > 1) {
                final String mimeType = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens()) {
                    final String fileExt = tokenizer.nextToken();
                    this._table.put(fileExt.toLowerCase(), mimeType);
                }
            }
        }
        reader.close();
    }
    
    public String getMIMEType(final String fileExt) {
        return (String) this._table.get(fileExt.toLowerCase());
    }
    
    public static void main(final String[] argv) {
        if (argv.length == 0) {
            System.out.println("Usage: [-f filename] fileExt1 fileExt2 ...");
            System.exit(-1);
        }
        FileExtensionToMIMEMapper mapper = null;
        try {
            int extStart = 0;
            if (argv[0].equals("-f")) {
                mapper = new FileExtensionToMIMEMapper(new URI(argv[1]));
                extStart = 2;
            }
            else {
                mapper = new FileExtensionToMIMEMapper();
            }
            while (extStart < argv.length) {
                final String fileExt = argv[extStart];
                System.out.println(fileExt + " --> " + mapper.getMIMEType(fileExt));
                ++extStart;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
