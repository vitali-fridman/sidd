// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.nio.charset.CharsetEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

public class StringTruncator
{
    private final String charsetName;
    
    public StringTruncator(final String charsetName) {
        this.charsetName = charsetName;
    }
    
    public String truncate(final String s, final int length) {
        final byte[] bytes = new byte[length];
        final ByteBuffer outputBuffer = ByteBuffer.wrap(bytes);
        final CharBuffer inputBuffer = CharBuffer.wrap(s.toCharArray());
        final Charset charset = Charset.forName(this.charsetName);
        final CharsetEncoder encoder = charset.newEncoder();
        encoder.encode(inputBuffer, outputBuffer, true);
        String truncatedString;
        try {
            truncatedString = new String(bytes, 0, outputBuffer.position(), this.charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return truncatedString;
    }
}
