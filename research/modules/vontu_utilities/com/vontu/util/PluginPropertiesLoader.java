// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.IOException;
import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SystemProperties;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SystemSettingProvider;
import com.vontu.util.config.SettingReader;

public class PluginPropertiesLoader
{
    private final SettingReader settings;
    
    public PluginPropertiesLoader() {
        this.settings = new SettingReader(new SystemSettingProvider());
    }
    
    public void loadPluginProperties(final PluginDescriptor<?> descriptor) {
        try {
            SystemProperties.load(this.settings.getSetting(descriptor.getPluginProperties()));
        }
        catch (IOException e) {
            throw new ConfigurationException("Failed to load plug-in configuration properties.", e);
        }
    }
}
