// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.util;

public class IndexUtil
{
    public static final String INCREMENTAL_SUFFIX = ".i";
    public static final int MAGIC_NUMBER = 62052;
    public static final int MD5_SIZE = 16;
    
    public static byte[] bytesFromCharacters(final CharSequence normalizedText) {
        final byte[] bt = new byte[normalizedText.length() * 2];
        for (int i = 0; i < normalizedText.length(); ++i) {
            bt[i * 2] = (byte)(normalizedText.charAt(i) >>> 8 & '\u00ff');
            bt[i * 2 + 1] = (byte)(normalizedText.charAt(i) & '\u00ff');
        }
        return bt;
    }
}
