// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

public class PropertiesFileConverter
{
    public static String convertToAscii(final String toConvert) {
        if (toConvert == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final char c : toConvert.toCharArray()) {
            if (c <= '~') {
                sb.append(c);
            }
            else {
                sb.append("\\u");
                final String hex = Integer.toHexString(c);
                for (int j = hex.length(); j < 4; ++j) {
                    sb.append('0');
                }
                sb.append(hex);
            }
        }
        return sb.toString();
    }
}
