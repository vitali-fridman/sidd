// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.exceptionmatcher;

import java.util.Hashtable;
import java.util.Map;

import com.vontu.util.config.PropertySettingProvider;

import java.util.Enumeration;
import java.util.regex.PatternSyntaxException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.io.File;
import java.util.logging.Logger;

public class ExceptionMatcherConfig
{
    private Logger _logger;
    private File _propertyFile;
    private Properties _properties;
    private final HashMap<String, ArrayList<Pattern>> _map;
    
    public ExceptionMatcherConfig(final String pathSystemProperty) {
        this(pathSystemProperty, Logger.getLogger(ExceptionMatcherConfig.class.getName()));
    }
    
    public ExceptionMatcherConfig(final File propertyFile, final Logger logger) throws ExceptionMatcherConfigException {
        this._map = new HashMap<String, ArrayList<Pattern>>();
        this._logger = logger;
        this._propertyFile = propertyFile;
        this._properties = this.loadProperties();
        this.loadPatterns();
    }
    
    public ExceptionMatcherConfig(final String pathSystemProperty, final Logger logger) throws ExceptionMatcherConfigException {
        this._map = new HashMap<String, ArrayList<Pattern>>();
        this._logger = logger;
        final String propertyFilePath = System.getProperty(pathSystemProperty);
        if (propertyFilePath == null) {
            this.logWarning("Configuration property " + pathSystemProperty + " isn't set." + " Using default configuration values.");
            this._propertyFile = null;
            return;
        }
        this._propertyFile = new File(propertyFilePath);
        this._properties = this.loadProperties();
        this.loadPatterns();
    }
    
    public ExceptionMatcherConfig(final Properties properties, final Logger logger) throws ExceptionMatcherConfigException {
        this._map = new HashMap<String, ArrayList<Pattern>>();
        this._properties = properties;
        this._logger = logger;
        this.loadPatterns();
    }
    
    public ArrayList<Pattern> getPatterns(final String exceptionClass) {
        return this._map.get(exceptionClass);
    }
    
    private void loadPatterns() throws ExceptionMatcherConfigException {
        final Enumeration<Object> keys = this._properties.keys();
        Pattern pattern = null;
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            this._logger.log(Level.FINE, "Processing pattern for key " + key + ".");
            final String regex = this._properties.getProperty(key);
            final String[] prefix = key.split("\\Q.\\E[0-9]");
            if (prefix != null) {
                final String exceptionClass = prefix[0];
                try {
                    pattern = Pattern.compile(regex);
                    this._logger.log(Level.FINE, "Loaded pattern '" + pattern.pattern() + "' for exception class " + exceptionClass);
                }
                catch (PatternSyntaxException e) {
                    this._logger.log(Level.SEVERE, e.getMessage());
                    throw new ExceptionMatcherConfigException("Failed to compile pattern " + regex + ".", e);
                }
                ArrayList<Pattern> list = this._map.get(exceptionClass);
                if (list == null) {
                    list = new ArrayList<Pattern>();
                    this._map.put(exceptionClass, list);
                }
                list.add(pattern);
            }
        }
    }
    
    private Properties loadProperties() {
        final Properties properties = new Properties();
        final PropertySettingProvider configMatcherSettings = new PropertySettingProvider(this._propertyFile, null);
        properties.putAll(configMatcherSettings.getPropertyMap());
        return properties;
    }
    
    private void logWarning(final String message) {
        if (this._logger != null) {
            this._logger.warning(message);
        }
    }
}
