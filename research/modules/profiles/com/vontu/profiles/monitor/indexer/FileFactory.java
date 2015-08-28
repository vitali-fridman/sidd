// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.io.File;

class FileFactory
{
    private static final String TMP_SUFFIX = ".tmp";
    private final File _targetFolder;
    
    FileFactory(final File targetFolder) {
        this._targetFolder = targetFolder;
    }
    
    File getFile(final String filePath) {
        return new File(filePath);
    }
    
    File getTargetFile(final String targetFileName) {
        return new File(this._targetFolder, targetFileName);
    }
    
    File getTempFile(final String targetFileName) {
        return new File(this._targetFolder, targetFileName + ".tmp");
    }
}
