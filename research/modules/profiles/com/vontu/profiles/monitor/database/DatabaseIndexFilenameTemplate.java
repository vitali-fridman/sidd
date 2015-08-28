// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.database;

import java.util.regex.Pattern;
import com.vontu.profiles.common.IndexFilenameTemplate;

public class DatabaseIndexFilenameTemplate implements IndexFilenameTemplate
{
    public static final IndexFilenameTemplate instance;
    private static final String INDEX_FILENAME_EXTENSION = ".rdx";
    private static final String INDEX_FILENAME_PREFIX = "DataSource";
    private static final Pattern _fileMask;
    
    @Override
    public String createBaseFileName(final String profileId, final int indexVersion) {
        return "DataSource." + profileId + '.' + indexVersion + ".rdx";
    }
    
    @Override
    public Pattern createFileMask() {
        return DatabaseIndexFilenameTemplate._fileMask;
    }
    
    @Override
    public String createEndpointBaseFileName(final String profileId, final int version) {
        return "";
    }
    
    static {
        instance = new DatabaseIndexFilenameTemplate();
        _fileMask = Pattern.compile("DataSource.*\\.rdx(\\.\\d)?");
    }
}
