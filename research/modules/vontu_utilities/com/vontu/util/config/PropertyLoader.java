// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.io.IOException;
import java.io.File;
import java.util.Properties;

public class PropertyLoader
{
    public static Properties loadProperties(final String propertyFilePath) throws ConfigurationException {
        return loadProperties(new File(propertyFilePath), null);
    }
    
    public static Properties loadProperties(final File propertyFile) throws ConfigurationException {
        return loadProperties(propertyFile, null);
    }
    
    public static Properties loadProperties(final String defaultPropertyFilePath, final String customPropertyFilePath) throws ConfigurationException {
        return loadProperties(new File(defaultPropertyFilePath), new File(customPropertyFilePath));
    }
    
    public static Properties loadProperties(final File defaultPropertyFile, final File customPropertyFile) {
        final Properties properties = new Properties();
        loadCustomAndDefaultProperties(defaultPropertyFile, customPropertyFile, properties);
        return properties;
    }
    
    public static OrderedProperties loadOrderedProperties(final File defaultPropertyFile, final File customPropertyFile) {
        final OrderedProperties properties = new OrderedProperties();
        loadCustomAndDefaultProperties(defaultPropertyFile, customPropertyFile, properties);
        return properties;
    }
    
    private static void loadCustomAndDefaultProperties(final File defaultPropertyFile, final File customPropertyFile, final Properties properties) {
        try {
            loadProperties(properties, defaultPropertyFile);
        }
        catch (IOException e) {
            throw new ConfigurationException("Failed to load default properties from " + defaultPropertyFile.getAbsolutePath() + '.', e);
        }
        if (customPropertyFile != null) {
            try {
                final Properties customProperties = new Properties();
                loadProperties(customProperties, customPropertyFile);
                properties.putAll(customProperties);
            }
            catch (IOException e) {
                throw new ConfigurationException("Failed to load custom properties from " + customPropertyFile.getAbsolutePath() + '.', e);
            }
        }
    }
    
    private static void loadProperties(final Properties properties, final File propertyFile) throws IOException {
        final BufferedInputStream is = new BufferedInputStream(new FileInputStream(propertyFile));
        try {
            properties.load(is);
        }
        finally {
            is.close();
        }
    }
}
