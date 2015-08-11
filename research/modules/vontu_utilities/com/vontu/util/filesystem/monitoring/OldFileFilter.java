// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem.monitoring;

import java.io.File;
import com.vontu.util.filesystem.FileExtensionBasedFilter;
import java.io.FilenameFilter;
import com.vontu.util.filesystem.AbandonedFileFilter;

public class OldFileFilter extends AbandonedFileFilter implements FilenameFilter
{
    private final FilenameFilter filenameFilter;
    
    public OldFileFilter(final long expirationInterval, final String fileExtension) {
        this(expirationInterval, new FileExtensionBasedFilter(fileExtension));
    }
    
    public OldFileFilter(final long expirationInterval, final FilenameFilter filenameFilter) {
        super(expirationInterval);
        this.filenameFilter = filenameFilter;
    }
    
    @Override
    public boolean accept(final File file) {
        return this.filenameFilter.accept(file.getParentFile(), file.getName()) && super.accept(file);
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return this.accept(new File(dir, name));
    }
}
