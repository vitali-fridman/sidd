// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.SortedSet;
import java.util.Set;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemProperties
{
    private static Logger logger;
    
    public static void load() throws FileNotFoundException, IOException {
        final String protectPropertiesFilePath = System.getProperty("com.vontu.properties");
        if (null == protectPropertiesFilePath || 0 == protectPropertiesFilePath.length()) {
            SystemProperties.logger.log(Level.SEVERE, "Unable to load SystemProperties, property 'com.vontu.properties' is not defined");
            throw new ConfigurationException("Unable to load SystemProperties, property 'com.vontu.properties' is not defined");
        }
        final File protectPropertiesFile = new File(protectPropertiesFilePath);
        final PropertySettingProvider systemPropertyProvider = new PropertySettingProvider(protectPropertiesFile, SystemProperties.logger);
        setSystemProperties(systemPropertyProvider);
    }
    
    public static void load(final String configFilePath) throws FileNotFoundException, IOException {
        if (configFilePath == null) {
            SystemProperties.logger.log(Level.WARNING, "No file passed to SystemProperties, no properties will be loaded.");
            return;
        }
        final File configFile = new File(configFilePath);
        final PropertySettingProvider systemPropertyProvider = new PropertySettingProvider(configFile, SystemProperties.logger);
        setSystemProperties(systemPropertyProvider);
    }
    
    public static void clear(final String path) throws FileNotFoundException, IOException {
        File systemPropertiesFile = new File(path);
        systemPropertiesFile = systemPropertiesFile.getAbsoluteFile();
        final FileInputStream propertiesStream = new FileInputStream(systemPropertiesFile);
        final Properties properties = new Properties();
        try {
            properties.load(propertiesStream);
            for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                final String name = (String) entry.getKey();
                if (System.getProperty(name) != null) {
                    System.getProperties().remove(name);
                }
            }
        }
        finally {
            propertiesStream.close();
        }
    }
    
    static void setSystemProperties(final PropertySettingProvider provider) throws IOException {
        StringBuffer logOutput = null;
        if (SystemProperties.logger.isLoggable(Level.INFO)) {
            logOutput = new StringBuffer();
            logOutput.append("System Properties:");
        }
        final Set<String> propertyNames = provider.getKeySet();
        final SortedSet<String> sortedPropertyNames = new TreeSet<String>();
        sortedPropertyNames.addAll((Collection<? extends String>)propertyNames);
        for (final String name : sortedPropertyNames) {
            final String value = provider.getSetting(name);
            if (System.getProperty(name) == null) {
                System.setProperty(name, value);
                if (logOutput == null || name.contains("password")) {
                    continue;
                }
                logOutput.append(System.getProperty("line.separator"));
                logOutput.append("  ");
                logOutput.append(name);
                logOutput.append('=');
                logOutput.append(value);
            }
        }
        if (logOutput != null) {
            SystemProperties.logger.info(logOutput.toString());
        }
    }
    
    static {
        SystemProperties.logger = Logger.getLogger(SystemProperties.class.getName());
    }
}
