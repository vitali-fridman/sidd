// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.File;
import java.io.FileFilter;

public class AbandonedFileFilter implements FileFilter
{
    private long _retentionInterval;
    
    public AbandonedFileFilter(final long retentionInterval) {
        this._retentionInterval = retentionInterval;
    }
    
    @Override
    public boolean accept(final File file) {
        return System.currentTimeMillis() - file.lastModified() > this._retentionInterval;
    }
}
