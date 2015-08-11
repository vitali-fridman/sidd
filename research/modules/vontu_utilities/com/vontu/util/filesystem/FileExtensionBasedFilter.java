// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionBasedFilter implements FilenameFilter
{
    private String fileExtension;
    
    public FileExtensionBasedFilter(final String extenstionString) {
        this.fileExtension = extenstionString;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return name.endsWith(this.fileExtension);
    }
}
