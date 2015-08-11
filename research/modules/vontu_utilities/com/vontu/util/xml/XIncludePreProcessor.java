// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.io.Reader;
import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import com.vontu.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.SAXParserFactory;
import com.vontu.util.URILoader;
import java.net.URI;
import org.xml.sax.Attributes;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.xml.sax.Locator;
import java.io.Writer;
import org.xml.sax.helpers.DefaultHandler;

public class XIncludePreProcessor extends DefaultHandler
{
    private Writer _documentWriter;
    private String _encoding;
    private boolean _elementsOnly;
    private Locator _locator;
    private StringBuffer _log;
    private Logger _logger;
    private static final String XINCLUDE_NAMESPACE = "http://www.w3.org/2001/XInclude";
    
    public XIncludePreProcessor(final OutputStream outputStream) {
        this(outputStream, null);
    }
    
    public XIncludePreProcessor(final OutputStream outputStream, final String encoding) {
        this._locator = null;
        this._log = new StringBuffer();
        this._logger = Logger.getLogger(XIncludePreProcessor.class.getName());
        this._encoding = encoding;
        this._elementsOnly = false;
        if (encoding != null) {
            this._encoding = encoding;
        }
        else {
            this._encoding = "UTF-8";
        }
        this._documentWriter = new OutputStreamWriter(outputStream, Charset.forName(this._encoding));
    }
    
    private XIncludePreProcessor(final Writer documentWriter, final String encoding, final boolean elementsOnly) {
        this._locator = null;
        this._log = new StringBuffer();
        this._logger = Logger.getLogger(XIncludePreProcessor.class.getName());
        this._encoding = encoding;
        this._elementsOnly = elementsOnly;
        this._documentWriter = documentWriter;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this._locator = locator;
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (!this._elementsOnly) {
            this.write("<?xml version=\"1.0\" encoding=\"");
            this.write(this._encoding);
            this.write("\" ?>");
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.flush();
        if (this._logger.isLoggable(Level.FINEST)) {
            this._logger.log(Level.FINEST, this._log.toString());
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            if (localName.equals("include") && namespaceURI.equals("http://www.w3.org/2001/XInclude")) {
                final String href = atts.getValue("href");
                if (href == null) {
                    String message = "element: " + qName + " attribute: href missing";
                    if (this._locator != null) {
                        message = message + " line: " + this._locator.getLineNumber() + " col: " + this._locator.getColumnNumber();
                    }
                    throw new SAXException(message);
                }
                final InputStream includeXML = URILoader.load(new URI(href));
                this.flush();
                final String parse = atts.getValue("parse");
                if (parse == null || parse.equals("xml")) {
                    final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    parserFactory.setNamespaceAware(true);
                    final SAXParser parser = parserFactory.newSAXParser();
                    parser.parse(includeXML, new XIncludePreProcessor(this._documentWriter, this._encoding, true));
                    includeXML.close();
                }
                else if (parse.equals("text")) {
                    String encoding = atts.getValue("encoding");
                    if (encoding == null) {
                        encoding = "UTF-8";
                    }
                    final Reader reader = new InputStreamReader(includeXML, Charset.forName(encoding));
                    this.write("<![CDATA[");
                    int c;
                    while ((c = reader.read()) != -1) {
                        this.write(c);
                    }
                    this.write("]]>");
                    reader.close();
                }
                else {
                    if (!parse.equals("binary")) {
                        String message2 = "element: " + qName + " attribute: parse=" + parse + " [unsupported value]";
                        if (this._locator != null) {
                            message2 = message2 + " line: " + this._locator.getLineNumber() + " col: " + this._locator.getColumnNumber();
                        }
                        throw new SAXException(message2);
                    }
                    final ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
                    int b;
                    while ((b = includeXML.read()) != -1) {
                        binaryData.write(b);
                    }
                    binaryData.close();
                    includeXML.close();
                    final byte[] binaryDataArray = binaryData.toByteArray();
                    final String base64Encoded = Base64.encodeBytes(binaryDataArray);
                    this.write(base64Encoded);
                }
            }
            else {
                this.write(60);
                this.write(qName);
                for (int attributeCount = atts.getLength(), i = 0; i < attributeCount; ++i) {
                    this.write(32);
                    this.write(atts.getQName(i));
                    this.write("=\"");
                    final String attributeValue = atts.getValue(i);
                    for (int attributeValueLength = attributeValue.length(), j = 0; j < attributeValueLength; ++j) {
                        final char c2 = attributeValue.charAt(j);
                        switch (c2) {
                            case '<': {
                                this.write("&lt;");
                                break;
                            }
                            case '>': {
                                this.write("&gt;");
                                break;
                            }
                            case '&': {
                                this.write("&amp;");
                                break;
                            }
                            case '\'': {
                                this.write("&apos;");
                                break;
                            }
                            case '\"': {
                                this.write("&quot;");
                                break;
                            }
                            default: {
                                this.write(c2);
                                break;
                            }
                        }
                    }
                    this.write(34);
                }
                this.write(62);
            }
        }
        catch (IOException ioException) {
            throw new SAXException(ioException);
        }
        catch (ParserConfigurationException parserConfigurationException) {
            throw new SAXException(parserConfigurationException);
        }
        catch (URISyntaxException uriSyntaxException) {
            throw new SAXException(uriSyntaxException);
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (!localName.equals("include") || !namespaceURI.equals("http://www.w3.org/2001/XInclude")) {
            this.write("</");
            this.write(qName);
            this.write(62);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        for (int count = start + length, i = start; i < count; ++i) {
            final char c = ch[i];
            switch (c) {
                case '<': {
                    this.write("&lt;");
                    break;
                }
                case '>': {
                    this.write("&gt;");
                    break;
                }
                case '&': {
                    this.write("&amp;");
                    break;
                }
                case '\'': {
                    this.write("&apos;");
                    break;
                }
                case '\"': {
                    this.write("&quot;");
                    break;
                }
                default: {
                    this.write(c);
                    break;
                }
            }
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (!this._elementsOnly) {
            this.write("<?");
            this.write(target);
            this.write(32);
            this.write(data);
            this.write("?>");
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    private void write(final String str) throws SAXException {
        try {
            this._documentWriter.write(str);
            if (this._logger.isLoggable(Level.FINEST)) {
                this._log.append(str);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    private void write(final int c) throws SAXException {
        try {
            this._documentWriter.write(c);
            if (this._logger.isLoggable(Level.FINEST)) {
                this._log.append((char)c);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }
    
    private void flush() throws SAXException {
        try {
            this._documentWriter.flush();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }
}
