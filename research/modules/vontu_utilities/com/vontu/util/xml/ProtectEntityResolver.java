// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import org.xml.sax.SAXException;
import com.vontu.util.URILoader;
import java.net.URI;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class ProtectEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicId, String systemId) throws SAXException, IOException {
        try {
            systemId = systemId.replace('\\', '/');
            final URI systemURI = new URI(systemId);
            final InputSource inputSource = new InputSource();
            inputSource.setEncoding("ISO-8859-1");
            inputSource.setByteStream(URILoader.load(systemURI));
            return inputSource;
        }
        catch (URISyntaxException e) {
            throw new SAXException("Malformed SYSTEM id: " + systemId, e);
        }
    }
}
