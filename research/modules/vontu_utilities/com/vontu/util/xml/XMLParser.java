// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import com.vontu.util.URILoader;
import java.net.URI;
import java.io.IOException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class XMLParser
{
    private SAXParser _parser;
    
    public XMLParser(final boolean validate) throws XMLParserException {
        try {
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(validate);
            this._parser = parserFactory.newSAXParser();
            this.setEntityResolver(new ProtectEntityResolver());
        }
        catch (Exception e) {
            throw new XMLParserException("An error occured creating and/or configuring the XML parser", e);
        }
    }
    
    public void setDTDHandler(final DTDHandler dtdHandler) throws XMLParserException {
        try {
            this._parser.getXMLReader().setDTDHandler(dtdHandler);
        }
        catch (SAXException e) {
            throw new XMLParserException("Unable to access the XML parser", e);
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) throws XMLParserException {
        try {
            this._parser.getXMLReader().setEntityResolver(entityResolver);
        }
        catch (SAXException e) {
            throw new XMLParserException("Unable to access the XML parser", e);
        }
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) throws XMLParserException {
        try {
            this._parser.getXMLReader().setErrorHandler(errorHandler);
        }
        catch (SAXException e) {
            throw new XMLParserException("Unable to access the XML parser", e);
        }
    }
    
    public void parse(final InputSource xmlDocument, final DefaultHandler documentHandler) throws XMLParserException {
        try {
            this._parser.parse(xmlDocument, documentHandler);
        }
        catch (SAXException e) {
            throw new XMLParserException("An error occured while parsing the XML document", e);
        }
        catch (IOException e2) {
            throw new XMLParserException("An error occured while reading from the XML document", e2);
        }
    }
    
    public void parse(final URI xmlDocumentURI, final DefaultHandler documentHandler) throws XMLParserException {
        InputStream xmlDocumentStream = null;
        try {
            xmlDocumentStream = URILoader.load(xmlDocumentURI);
            this.parse(new InputSource(xmlDocumentStream), documentHandler);
        }
        catch (FileNotFoundException e) {
            throw new XMLParserException("Could not find XML document: " + xmlDocumentURI.toString(), e);
        }
        catch (IOException e2) {
            throw new XMLParserException("Error opening XML document: " + xmlDocumentURI.toString(), e2);
        }
        finally {
            if (xmlDocumentStream != null) {
                try {
                    xmlDocumentStream.close();
                }
                catch (Exception e3) {
                    throw new XMLParserException("Error closing XML document: " + xmlDocumentURI.toString(), e3);
                }
            }
        }
    }
    
    public void parse(final String xmlDocumentURI, final DefaultHandler defaultHandler) throws XMLParserException {
        try {
            this.parse(new URI(xmlDocumentURI), defaultHandler);
        }
        catch (URISyntaxException e) {
            throw new XMLParserException("Invalid XML document URI: " + xmlDocumentURI, e);
        }
    }
}
