// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.document;

import com.vontu.profiles.common.DocIndexerUtil;
import java.util.regex.Pattern;
import com.vontu.profiles.common.IndexFilenameTemplate;

public class DocumentIndexFilenameTemplate implements IndexFilenameTemplate
{
    public static final IndexFilenameTemplate instance;
    private static final Pattern _fileMask;
    
    @Override
    public String createBaseFileName(final String profileId, final int indexVersion) {
        return DocIndexerUtil.getIndexFileName(profileId, indexVersion);
    }
    
    @Override
    public Pattern createFileMask() {
        return DocumentIndexFilenameTemplate._fileMask;
    }
    
    @Override
    public String createEndpointBaseFileName(final String profileId, final int version) {
        return DocIndexerUtil.getEndpointIndexFileName(Integer.parseInt(profileId), version);
    }
    
    static {
        instance = new DocumentIndexFilenameTemplate();
        _fileMask = Pattern.compile("DocSource.*\\.rdx(\\.\\d)?");
    }
}
