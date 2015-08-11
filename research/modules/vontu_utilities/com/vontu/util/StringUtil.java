// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Iterator;
import java.util.Collection;

public class StringUtil
{
    static final String ZEROES = "00000000000000000000000000000";
    
    public static String suffixOnce(final String current, final String suffix) {
        return current.endsWith(suffix) ? "" : suffix;
    }
    
    public static boolean endsWith(final StringBuffer buffer, final String suffix) {
        if (suffix.length() > buffer.length()) {
            return false;
        }
        int endIndex = suffix.length() - 1;
        int bufferIndex = buffer.length() - 1;
        while (endIndex >= 0) {
            if (buffer.charAt(bufferIndex) != suffix.charAt(endIndex)) {
                return false;
            }
            --bufferIndex;
            --endIndex;
        }
        return true;
    }
    
    public static String addZeroPadding(final String originalString, final int totalLength) {
        String zeroes;
        int zeroesRequired;
        for (zeroes = "00000000000000000000000000000", zeroesRequired = Math.max(totalLength - originalString.length(), 0); zeroes.length() < zeroesRequired; zeroes += zeroes) {}
        return zeroes.substring(0, zeroesRequired) + originalString;
    }
    
    public static String truncate(final String originalString, final int length) {
        if (originalString.length() > length) {
            return originalString.substring(0, length);
        }
        return originalString;
    }
    
    public static String join(final String separator, final Object... objects) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < objects.length; ++i) {
            if (i != 0) {
                result.append(separator);
            }
            result.append(objects[i].toString());
        }
        return result.toString();
    }
    
    public static <T> String join(final String separator, final Collection<T> items) {
        final StringBuffer result = new StringBuffer();
        boolean isFirstItem = true;
        for (final T item : items) {
            if (isFirstItem) {
                isFirstItem = false;
            }
            else {
                result.append(separator);
            }
            result.append(item.toString());
        }
        return result.toString();
    }
    
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }
    
    public static boolean isEmptyTrimmed(final String s) {
        return s == null || s.trim().length() == 0;
    }
    
    public static boolean isNotEmptyTrimmed(final String s) {
        return !isEmptyTrimmed(s);
    }
}
