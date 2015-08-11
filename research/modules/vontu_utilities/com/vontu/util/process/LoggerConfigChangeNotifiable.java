// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.File;

public interface LoggerConfigChangeNotifiable
{
    void onLoggingPropertiesUpdated(File p0);
    
    String getLoggingConfigChangeNotifiableDebugName();
}
