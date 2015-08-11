// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.Properties;
import java.io.File;
import java.util.logging.Logger;

public class OrderedPropertySettingProvider extends PropertySettingProvider
{
    public OrderedPropertySettingProvider(final String pathSystemProperty, final boolean resolvePaths) {
        super(pathSystemProperty, (Logger)null, resolvePaths);
    }
    
    protected static Properties loadProperties(final File defaultPropertyFile, final File customPropertyFile, final SafeLogger logger) {
        try {
            final Properties properties = PropertyLoader.loadOrderedProperties(defaultPropertyFile, customPropertyFile);
            return properties;
        }
        catch (ConfigurationException e) {
            logger.logWarning("Configuration file \"" + defaultPropertyFile.getAbsolutePath() + "\" can't be read." + " Using hard-coded configuration values.");
            return new OrderedProperties();
        }
    }
}
