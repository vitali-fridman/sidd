// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.io.InputStream;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import com.vontu.util.URILoader;
import java.net.URI;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

public class ProtectURIResolver implements URIResolver
{
    @Override
    public Source resolve(final String href, final String base) throws TransformerException {
        try {
            final URI uri = new URI(href);
            final InputStream is = URILoader.load(uri);
            final Source fileSource = new StreamSource(is, uri.toString());
            return fileSource;
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
    }
}
