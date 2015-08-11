// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.util.logging.Logger;

public class LoggingCustomConfig
{
    private static Logger logger;
    
    public LoggingCustomConfig() throws IOException, SecurityException {
        final String loggingConfigFile = System.getProperty("java.util.logging.config.file");
        final PropertySettingProvider logSettings = new PropertySettingProvider(new File(loggingConfigFile), null);
        final Properties loggingProperties = new Properties();
        final Map<String, String> configSettingMap = logSettings.getPropertyMap();
        loggingProperties.putAll(configSettingMap);
        final ByteArrayOutputStream propertyOutput = new ByteArrayOutputStream();
        loggingProperties.store(propertyOutput, null);
        final ByteArrayInputStream configSettingStream = new ByteArrayInputStream(propertyOutput.toByteArray());
        final LogManager manager = LogManager.getLogManager();
        manager.readConfiguration(configSettingStream);
    }
    
    static {
        LoggingCustomConfig.logger = Logger.getLogger(SystemProperties.class.getName());
    }
}
