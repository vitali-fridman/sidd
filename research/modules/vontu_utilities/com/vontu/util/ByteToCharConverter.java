// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class ByteToCharConverter
{
    public static final String DEFAULT_CHARACTER_SET = "ISO-8859-1";
    
    public static char[] convertAll(final byte[] input) throws CharacterCodingException {
        return convertAll(input, "ISO-8859-1");
    }
    
    public static char[] convertAll(final byte[] input, final String charsetName) throws CharacterCodingException {
        final ByteBuffer bb = ByteBuffer.wrap(input);
        final Charset cs = Charset.forName(charsetName);
        final CharsetDecoder cd = cs.newDecoder();
        final CharBuffer cb = cd.decode(bb);
        return cb.array();
    }
}
