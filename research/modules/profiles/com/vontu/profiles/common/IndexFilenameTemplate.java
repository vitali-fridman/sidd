// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import java.util.regex.Pattern;

public interface IndexFilenameTemplate
{
    String createBaseFileName(String p0, int p1);
    
    Pattern createFileMask();
    
    String createEndpointBaseFileName(String p0, int p1);
}
