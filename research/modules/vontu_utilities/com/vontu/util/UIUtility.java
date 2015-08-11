// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class UIUtility
{
    public static final String ELIPSES = "...";
    
    public static String truncateToMaxLength(final String toTruncate, final int maxLength) {
        if (toTruncate.length() <= maxLength) {
            return toTruncate;
        }
        if (maxLength > "...".length()) {
            String newString = toTruncate.substring(0, maxLength - "...".length());
            newString += "...";
            return newString;
        }
        return toTruncate.substring(0, maxLength);
    }
}
