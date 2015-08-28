// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.io.File;
import java.util.regex.Pattern;
import java.io.FilenameFilter;

class IndexFilenameFilter implements FilenameFilter
{
    private final Pattern _filenamePattern;
    
    IndexFilenameFilter(final Pattern filenamePattern) {
        this._filenamePattern = filenamePattern;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return this._filenamePattern.matcher(name).matches();
    }
}
