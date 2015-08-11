// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.Hashtable;
import java.util.Set;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Properties;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Map;
import java.io.File;

public class PropertySettingProvider implements SettingProvider
{
    private final File _propertyFile;
    private final Map<String, String> _properties;
    
    public PropertySettingProvider(final String pathSystemProperty) {
        this(pathSystemProperty, null);
    }
    
    public PropertySettingProvider(final String pathSystemProperty, final boolean resolvePaths) {
        this(pathSystemProperty, (Logger)null, resolvePaths);
    }
    
    public PropertySettingProvider(final String pathSystemProperty, final Logger logger) {
        this(pathSystemProperty, new SafeLogger(logger), true);
    }
    
    public PropertySettingProvider(final String pathSystemProperty, final Logger logger, final boolean resolvePaths) {
        this(pathSystemProperty, new SafeLogger(logger), resolvePaths);
    }
    
    private PropertySettingProvider(final String pathSystemProperty, final SafeLogger safeLogger, final boolean resolvePaths) {
        this(getDefaultPropertyFile(pathSystemProperty, safeLogger), safeLogger, resolvePaths);
    }
    
    public PropertySettingProvider(final File defaultPropertyFile, final Logger logger) {
        this(defaultPropertyFile, new SafeLogger(logger), true);
    }
    
    public PropertySettingProvider(final File defaultPropertyFile, final Logger logger, final boolean resolvePaths) {
        this(defaultPropertyFile, new SafeLogger(logger), resolvePaths);
    }
    
    private PropertySettingProvider(final File defaultPropertyFile, final SafeLogger safeLogger, final boolean resolvePaths) {
        this(defaultPropertyFile, getCustomPropertyFile(defaultPropertyFile, safeLogger), safeLogger, resolvePaths);
    }
    
    private PropertySettingProvider(final File defaultPropertyFile, final File customPropertyFile, final SafeLogger safeLogger, final boolean resolvePaths) {
        File propertyFile;
        try {
            propertyFile = defaultPropertyFile.getCanonicalFile();
        }
        catch (IOException e) {
            safeLogger.logWarning("Cannot resolve cannonical path for configuration file \"" + defaultPropertyFile + " Using absolute path instead.");
            propertyFile = defaultPropertyFile.getAbsoluteFile();
        }
        this._propertyFile = propertyFile;
        this._properties = this.convertProperties(loadProperties(defaultPropertyFile, customPropertyFile, safeLogger), resolvePaths);
    }
    
    private static File getCustomPropertyFile(final File defaultPropertyFile, final SafeLogger safeLogger) {
        if (defaultPropertyFile == null || !defaultPropertyFile.exists()) {
            return null;
        }
        final String propertyFileName = defaultPropertyFile.getName();
        String customPropertyFilePath = System.getProperty("com.vontu.properties.custom.directory");
        if (customPropertyFilePath == null) {
            try {
                final Properties properties = PropertyLoader.loadProperties(defaultPropertyFile);
                customPropertyFilePath = properties.getProperty("com.vontu.properties.custom.directory");
                customPropertyFilePath = resolveRelativePath(customPropertyFilePath, defaultPropertyFile);
            }
            catch (ConfigurationException e) {
                customPropertyFilePath = null;
            }
            if (customPropertyFilePath == null) {
                safeLogger.logFine("Custom configuration directory not set. Using default configuration values.");
                return null;
            }
        }
        final File customPropertyFile = new File(customPropertyFilePath, propertyFileName);
        if (!customPropertyFile.exists()) {
            safeLogger.logFine("Custom configuration file " + customPropertyFile + " does not exist. " + "Using default configuration values.");
            return null;
        }
        return customPropertyFile;
    }
    
    private static File getDefaultPropertyFile(final String systemProperty, final SafeLogger safeLogger) {
        String propertyFilePath;
        if (systemProperty == null) {
            safeLogger.logWarning("Configuration property " + systemProperty + " isn't set." + " Using default configuration values.");
            propertyFilePath = System.getProperty("user.dir");
        }
        else {
            propertyFilePath = System.getProperty(systemProperty);
            if (propertyFilePath == null) {
                safeLogger.logWarning("Configuration property " + systemProperty + " isn't set." + " Using default configuration values.");
                propertyFilePath = System.getProperty("user.dir");
            }
            else if (propertyFilePath.startsWith("file:")) {
                try {
                    return new File(new URI(propertyFilePath));
                }
                catch (URISyntaxException ex) {}
            }
        }
        return new File(propertyFilePath);
    }
    
    private Map<String, String> convertProperties(final Properties properties, final boolean resolvePaths) {
        final Map<String, String> map = new HashMap<String, String>(properties.size());
        for (final Object propertyKey : properties.keySet()) {
            if (propertyKey instanceof String) {
                final String propertyName = (String)propertyKey;
                String propertyValue = properties.getProperty(propertyName);
                if (propertyValue != null) {
                    propertyValue = propertyValue.trim();
                }
                if (resolvePaths) {
                    if (propertyName.endsWith(".dir") || propertyName.endsWith(".file") || propertyName.endsWith(".properties") || propertyName.endsWith(".home")) {
                        propertyValue = resolveRelativePath(propertyValue, this._propertyFile);
                    }
                    if (propertyName.endsWith(".uri")) {
                        propertyValue = this.resolveURI(propertyValue);
                    }
                }
                map.put(propertyName, propertyValue);
            }
        }
        return map;
    }
    
    public void resolvePropertyRelativePath(final String propertyName) {
        if (this._properties.containsKey(propertyName)) {
            final String propertyValue = this._properties.get(propertyName);
            final String resolvedPropertyValue = resolveRelativePath(propertyValue, this._propertyFile);
            if (null != resolvedPropertyValue) {
                this._properties.put(propertyName, resolvedPropertyValue);
            }
        }
    }
    
    protected static Properties loadProperties(final File defaultPropertyFile, final File customPropertyFile, final SafeLogger logger) {
        try {
            final Properties properties = PropertyLoader.loadProperties(defaultPropertyFile, customPropertyFile);
            return properties;
        }
        catch (ConfigurationException e) {
            logger.logWarning("Configuration file \"" + defaultPropertyFile.getAbsolutePath() + "\" can't be read." + " Using hard-coded configuration values.");
            return new Properties();
        }
    }
    
    @Override
    public String getSetting(final String name) {
        return this._properties.get(name);
    }
    
    private static String resolveRelativePath(final String value, final File propertyFile) {
        if (value == null) {
            return null;
        }
        try {
            return new File(value).isAbsolute() ? value : new File(propertyFile.getParent(), value).getCanonicalPath();
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public Map<String, String> getPropertyMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this._properties);
    }
    
    private String resolveURI(String value) {
        if (value == null) {
            return null;
        }
        try {
            final String normalizedPropertiesDir = this._propertyFile.getParent().replace(File.separatorChar, '/');
            final URI propertiesURI = new URI("file", null, ((normalizedPropertiesDir.charAt(0) != '/') ? "/" : "") + normalizedPropertiesDir + "/", null, null);
            final URI uri = new URI(value.replace('\\', '/'));
            if (!uri.isAbsolute()) {
                final URI absoluteURI = propertiesURI.resolve(uri);
                value = absoluteURI.toString();
            }
        }
        catch (URISyntaxException e) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, e);
        }
        return value;
    }
    
    public Set<Map.Entry<String, String>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<String, String>>)this._properties.entrySet());
    }
    
    public Set<String> getKeySet() {
        return Collections.unmodifiableSet((Set<? extends String>)this._properties.keySet());
    }
    
    public File getPropertyFile() {
        return this._propertyFile;
    }
    
    protected static class SafeLogger
    {
        private final Logger _logger;
        
        SafeLogger(final Logger logger) {
            this._logger = logger;
        }
        
        void logWarning(final String message) {
            if (this._logger != null) {
                this._logger.warning(message);
            }
        }
        
        void logFine(final String message) {
            if (this._logger != null) {
                this._logger.fine(message);
            }
        }
    }
}
