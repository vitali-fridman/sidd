// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.Enumeration;
import java.io.IOException;

import com.vontu.util.URILoader;

import java.net.URI;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class ResourceSettingProvider implements SettingProvider
{
    private final Logger _logger;
    private final Map<String, String> _defaultSettings;
    private final Map<String, String> _overwriteMapping;
    private final SettingProvider _overwriteSettings;
    
    public ResourceSettingProvider(final SettingProvider settings, final Logger logger, final String propertiesFile, final Map<String, String> overwriteMapping) {
        this._logger = logger;
        this._defaultSettings = this.loadProperties(propertiesFile);
        this._overwriteMapping = overwriteMapping;
        this._overwriteSettings = settings;
    }
    
    public ResourceSettingProvider(final Logger logger, final String propertiesFile, final Map<String, String> overwriteMapping) {
        this(null, logger, propertiesFile, overwriteMapping);
    }
    
    @Override
    public String getSetting(final String key) {
        if (key == null) {
            this.log(Level.INFO, "Got null key, ignoring.");
            return null;
        }
        if (this._overwriteSettings != null) {
            String overwriteKey = null;
            if (this._overwriteMapping != null) {
                overwriteKey = this._overwriteMapping.get(key);
            }
            final String value = this._overwriteSettings.getSetting((overwriteKey == null) ? key : overwriteKey);
            if (value != null) {
                return value;
            }
        }
        this.log(Level.FINE, "Setting <" + key + "> not set. Using default.");
        return this._defaultSettings.get(key.toLowerCase());
    }
    
    private Map<String, String> loadProperties(final String propertiesFile) {
        final Map<String, String> out = new HashMap<String, String>();
        if (propertiesFile == null) {
            this.log(Level.FINE, "No properties file provided.");
            return out;
        }
        final Properties properties = new Properties();
        final URI uri = URI.create("resource:" + propertiesFile);
        try {
            properties.load(URILoader.load(uri));
        }
        catch (IOException e) {
            this.log(Level.WARNING, "Could not load properties file: " + propertiesFile);
            return out;
        }
        final Enumeration keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            out.put(key.toLowerCase(), properties.getProperty(key));
        }
        return out;
    }
    
    private void log(final Level level, final String message) {
        if (this._logger != null) {
            this._logger.log(level, message);
        }
    }
}
