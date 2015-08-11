// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import java.util.logging.Level;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class SettingReader
{
    private static final int KB = 1024;
    private static final int MB = 1048576;
    private static final int GB = 1073741824;
    private final Logger _logger;
    private final SettingProvider _settings;
    private final Set<String> _reportedWarnings;
    
    public SettingReader(final SettingProvider settings) {
        this(settings, null);
    }
    
    public SettingReader(final SettingProvider settings, final Logger logger) throws IllegalArgumentException {
        this._reportedWarnings = new HashSet<String>();
        if (settings == null) {
            throw new IllegalArgumentException();
        }
        this._settings = settings;
        this._logger = logger;
    }
    
    public String checkSetting(final String value, final String name, final String defaultValue) throws IllegalArgumentException {
        if (name == null || defaultValue == null) {
            throw new IllegalArgumentException();
        }
        if (value == null) {
            this.logDefaultSetting("Configuration parameter \"" + name + "\" isn't set. Using the default value of " + defaultValue + '.', name);
            return defaultValue;
        }
        return value;
    }
    
    private static File checkFile(final String name, final String path) throws ConfigurationException {
        final File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new ConfigurationException("The value \"" + path + "\" of configuration property \"" + name + "\" doesn't specify a valid file.");
        }
        return file;
    }
    
    private static File checkFolder(final String name, final String path) throws ConfigurationException {
        final File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new InvalidPropertyValueException("The value \"" + path + "\" of configuration property \"" + name + "\" doesn't specify a valid folder.", name, path);
        }
        return folder;
    }
    
    public boolean getBooleanSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return parseBoolean(this.getSetting(name));
    }
    
    public boolean getBooleanSetting(final String name, final boolean defaultValue) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return parseBoolean(this.getSetting(name, String.valueOf(defaultValue)));
    }
    
    public int getIntSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return parseInteger(name, this.getSetting(name));
    }
    
    public int getIntSetting(final String name, final int defaultValue) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            return parseInteger(name, this.getSetting(name, String.valueOf(defaultValue)));
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of " + defaultValue + " instead.", name);
            return defaultValue;
        }
    }
    
    public double getDoubleSetting(final String name, final double defaultValue) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            return parseDouble(name, this.getSetting(name, String.valueOf(defaultValue)));
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of " + defaultValue + " instead.", name);
            return defaultValue;
        }
    }
    
    public long getLongSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return parseLong(name, this.getSetting(name));
    }
    
    public long getLongSetting(final String name, final long defaultValue) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            return parseLong(name, this.getSetting(name, String.valueOf(defaultValue)));
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of " + defaultValue + " instead.", name);
            return defaultValue;
        }
    }
    
    public long getMemorySetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        final String value = this.getSetting(name);
        return parseMemorySize(name, value);
    }
    
    public long getMemorySetting(final String name, final long defaultValue) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        final String value = this.getSetting(name, String.valueOf(defaultValue));
        try {
            return parseMemorySize(name, value);
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of " + defaultValue + " instead.", name);
            return defaultValue;
        }
    }
    
    public File getFileSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return checkFile(name, this.getSetting(name));
    }
    
    public File getFileSetting(final String name, final String defaultPath) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            return checkFile(name, this.getSetting(name, defaultPath));
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of \"" + defaultPath + "\" instead.", name);
            return checkFile(name, defaultPath);
        }
    }
    
    public File getFolderSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return checkFolder(name, this.getSetting(name));
    }
    
    public File getFolderSetting(final String name, final String defaultPath) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            return checkFolder(name, this.getSetting(name, defaultPath));
        }
        catch (ConfigurationException e) {
            this.logDefaultSetting(e.getMessage() + " Using the default value of \"" + defaultPath + "\" instead.", name);
            return checkFolder(name, defaultPath);
        }
    }
    
    public String getSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        final String value = this.getSettingValue(name);
        if (value == null) {
            throw new ConfigurationException("Configuration parameter \"" + name + "\" isn't set.");
        }
        return value;
    }
    
    public String getSetting(final String name, final String defaultValue) throws IllegalArgumentException {
        if (name == null || defaultValue == null) {
            throw new IllegalArgumentException();
        }
        final String value = this.getSettingValue(name);
        return this.checkSetting(value, name, defaultValue);
    }
    
    protected String getSettingValue(final String name) {
        return this._settings.getSetting(name);
    }
    
    private void logDefaultSetting(final String message, final String settingName) {
        if (this._logger != null && !this._reportedWarnings.contains(settingName)) {
            this._reportedWarnings.add(settingName);
            this._logger.fine(message);
        }
    }
    
    private static boolean parseBoolean(String value) {
        if (value != null) {
            value = value.trim();
        }
        return "on".equalsIgnoreCase(value) || Boolean.valueOf(value);
    }
    
    private static int parseInteger(final String name, final String value) throws ConfigurationException {
        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException("The value " + value + " of configuration property \"" + name + "\" isn't an integer.");
        }
    }
    
    private static double parseDouble(final String name, final String value) throws ConfigurationException {
        try {
            return Double.parseDouble(value.trim());
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException("The value " + value + " of configuration property \"" + name + "\" isn't a double.");
        }
    }
    
    private static long parseLong(final String name, final String value) throws ConfigurationException {
        try {
            return Long.parseLong(value.trim());
        }
        catch (NumberFormatException e) {
            throw new ConfigurationException("The value " + value + " of configuration property \"" + name + "\" isn't a long integer.");
        }
    }
    
    private static long parseMemorySize(final String name, final String value) throws ConfigurationException {
        String mantissa = value.trim();
        final char suffix = value.charAt(value.length() - 1);
        int exponent = 1;
        if (suffix == 'K' || suffix == 'k') {
            exponent = 1024;
            mantissa = value.substring(0, value.length() - 1);
        }
        else if (suffix == 'M' || suffix == 'm') {
            exponent = 1048576;
            mantissa = value.substring(0, value.length() - 1);
        }
        else if (suffix == 'G' || suffix == 'g') {
            exponent = 1073741824;
            mantissa = value.substring(0, value.length() - 1);
        }
        try {
            return Long.parseLong(mantissa) * exponent;
        }
        catch (Exception e) {
            throw new ConfigurationException("The value " + value + " of configuration property \"" + name + "\" doesn't represent an valid memory size.");
        }
    }
    
    public boolean settingExists(final String name) {
        return this.getSettingValue(name) != null;
    }
    
    public String getTrimmedSetting(final String name) throws ConfigurationException, IllegalArgumentException {
        return this.getSetting(name).trim();
    }
    
    public String getTrimmedSetting(final String name, final String defaultValue) {
        return this.getSetting(name, defaultValue).trim();
    }
    
    public SettingProvider getSettings() {
        return this._settings;
    }
    
    public Charset getCharsetSetting(final String settingName, final Charset defaultCharset) {
        try {
            return this.getCharset(this.getSetting(settingName), defaultCharset);
        }
        catch (ConfigurationException e) {
            final String message = "The setting for " + settingName + " was not found.  The " + "default charset [" + defaultCharset + "] will be used.";
            this.logWarning(message, e);
            return defaultCharset;
        }
    }
    
    private Charset getCharset(final String charsetName, final Charset defaultCharset) {
        this.logFine("Getting charset name for [" + charsetName + "].");
        final String messageTemplate = "The charset name [" + charsetName + "] is %s. " + "The default [" + defaultCharset + "] will " + "be used instead.";
        if (charsetName == null) {
            this.logWarning(String.format(messageTemplate, "null"));
            return defaultCharset;
        }
        final String charsetNameTrimmed = charsetName.trim();
        if (charsetNameTrimmed.isEmpty()) {
            this.logWarning(String.format(messageTemplate, "empty or whitespace"));
            return defaultCharset;
        }
        try {
            return Charset.forName(charsetNameTrimmed);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            this.logWarning(String.format(messageTemplate, "invalid or unsupported"), e);
            return defaultCharset;
        }
    }
    
    private void logWarning(final String message) {
        if (this._logger != null) {
            this._logger.warning(message);
        }
    }
    
    private void logWarning(final String message, final Throwable t) {
        if (this._logger != null) {
            this._logger.log(Level.WARNING, message, t);
        }
    }
    
    private void logFine(final String message) {
        if (this._logger != null) {
            this._logger.fine(message);
        }
    }
}
