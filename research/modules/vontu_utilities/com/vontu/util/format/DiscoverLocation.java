// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.format;

import java.util.Locale;

public class DiscoverLocation
{
    private static final String PST_FILE_EXTENSION = ".pst:";
    private final String filePath;
    
    public DiscoverLocation(final String discoverURL) {
        this.filePath = truncatePSTPath(discoverURL);
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    private static String truncatePSTPath(final String discoverURL) {
        if (discoverURL == null) {
            return null;
        }
        final String lowercaseURL = discoverURL.toLowerCase(Locale.ENGLISH);
        final int pstIndex = lowercaseURL.indexOf(".pst:");
        if (pstIndex >= 1) {
            return discoverURL.substring(0, pstIndex + ".pst:".length() - 1);
        }
        return discoverURL;
    }
}
