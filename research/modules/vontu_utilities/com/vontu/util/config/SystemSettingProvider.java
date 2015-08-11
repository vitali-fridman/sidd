// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.logging.Logger;

public final class SystemSettingProvider implements SettingProvider
{
    public static final SettingProvider instance;
    
    public static SettingReader getReader() {
        return new SettingReader(SystemSettingProvider.instance);
    }
    
    public static SettingReader getReader(final Logger logger) {
        return new SettingReader(new SystemSettingProvider(), logger);
    }
    
    @Override
    public String getSetting(final String name) {
        return System.getProperty(name);
    }
    
    static {
        instance = new SystemSettingProvider();
    }
}
