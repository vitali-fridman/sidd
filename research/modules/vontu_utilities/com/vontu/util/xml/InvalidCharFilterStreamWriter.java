// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.nio.CharBuffer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class InvalidCharFilterStreamWriter extends FilterXMLStreamWriter
{
    private final CodePointStreamFilter filter;
    private Text lastTextType;
    
    public InvalidCharFilterStreamWriter(final XMLStreamWriter writer) {
        super(writer);
        this.filter = new CodePointStreamFilter(new XMLCharacterFilter());
    }
    
    private void resetFilter(final Text currentTextType) {
        if (currentTextType == null || currentTextType != this.lastTextType) {
            this.filter.reset();
        }
        this.lastTextType = currentTextType;
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.resetFilter(Text.CDATA);
        final CharSequence filtered = this.filter.filterNextSequence(data);
        super.writeCData(filtered.toString());
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.resetFilter(Text.ELEMENT);
        final CharBuffer buffer = CharBuffer.wrap(text, start, len);
        final CharSequence filtered = this.filter.filterNextSequence(buffer);
        if (filtered == buffer) {
            super.writeCharacters(text, start, len);
        }
        else {
            super.writeCharacters(filtered.toString());
        }
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.resetFilter(Text.ELEMENT);
        final CharSequence filtered = this.filter.filterNextSequence(text);
        super.writeCharacters(filtered.toString());
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        this.resetFilter(Text.COMMENT);
        final CharSequence filtered = this.filter.filterNextSequence(data);
        super.writeComment(filtered.toString());
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.resetFilter(null);
        super.writeStartElement(localName);
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.resetFilter(null);
        super.writeStartElement(namespaceURI, localName);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.resetFilter(null);
        super.writeStartElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.resetFilter(null);
        super.writeEndElement();
    }
    
    private enum Text
    {
        ELEMENT, 
        CDATA, 
        COMMENT;
    }
}
