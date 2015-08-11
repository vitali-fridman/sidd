// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import java.nio.CharBuffer;
import java.io.IOException;
import java.io.Writer;
import java.io.FilterWriter;

public class XMLFilteringWriter extends FilterWriter
{
    private final CodePointStreamFilter filter;
    
    public XMLFilteringWriter(final Writer out) {
        super(out);
        this.filter = new CodePointStreamFilter(new XMLCharacterFilter());
    }
    
    @Override
    public void write(final int ch) throws IOException {
        final CharSequence single = String.valueOf((char)ch);
        final CharSequence filtered = this.filter.filterNextSequence(single);
        if (filtered.length() == 1) {
            super.write(filtered.charAt(0));
        }
        else if (filtered.length() > 1) {
            super.write(filtered.toString(), 0, filtered.length());
        }
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        final CharBuffer buffer = CharBuffer.wrap(cbuf, off, len);
        final CharSequence filtered = this.filter.filterNextSequence(buffer);
        if (filtered == buffer) {
            super.write(cbuf, off, len);
        }
        else {
            super.write(filtered.toString(), 0, filtered.length());
        }
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        final CharSequence filtered = this.filter.filterNextSequence(str.subSequence(off, off + len));
        super.write(filtered.toString(), 0, filtered.length());
    }
    
    @Override
    public void close() throws IOException {
        try {
            if (this.filter.hasRemainingCharacter()) {
                super.write(this.filter.getReplacementCharacter());
            }
        }
        finally {
            super.close();
        }
    }
}
