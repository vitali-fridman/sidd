// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.StringReader;
import com.vontu.util.CharSequenceReader;
import java.io.StringWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;

public class CharacterStreamReader
{
    public static void readAndCloseSource(final Reader source, final Writer destination) throws IOException {
        read(source, destination, true);
    }
    
    public static void read(final Reader source, final Writer destination) throws IOException {
        read(source, destination, false);
    }
    
    public static void read(final Reader source, final Writer destination, final boolean closeSource) throws IOException {
        if (null == source) {
            return;
        }
        final char[] buffer = new char[4096];
        for (int charsRead = source.read(buffer); charsRead != -1; charsRead = source.read(buffer)) {
            destination.write(buffer, 0, charsRead);
        }
        if (closeSource) {
            source.close();
        }
    }
    
    public static StringBuffer read(final Reader source) throws IOException {
        return read(source, true);
    }
    
    public static StringBuffer read(final Reader source, final boolean closeSource) throws IOException {
        if (null == source) {
            return null;
        }
        final StringWriter destination = new StringWriter();
        read(source, destination, closeSource);
        return destination.getBuffer();
    }
    
    public static String readToString(final Reader reader) throws IOException {
        final StringBuffer content = read(reader);
        if (null == content) {
            return null;
        }
        return content.toString();
    }
    
    public static Reader convertToReader(final StringWriter content) {
        if (null == content) {
            return null;
        }
        return new CharSequenceReader(content.getBuffer());
    }
    
    public static Reader convertToReader(final StringBuilder content) {
        if (null == content) {
            return null;
        }
        return new CharSequenceReader(content);
    }
    
    public static Reader convertToReader(final StringBuffer content) {
        if (null == content) {
            return null;
        }
        return new CharSequenceReader(content);
    }
    
    public static Reader convertToReader(final String content) {
        if (null == content) {
            return null;
        }
        return new StringReader(content);
    }
}
