// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.Map;
import com.vontu.util.config.PropertySettingProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionPoolConfig
{
    private static final String PROPERTY_FILE_PROPERTY = "com.vontu.util.jdbc.protectconnectionpool.properties.uri";
    private static final Logger _logger;
    private final int _maxActiveLimit;
    private final int _minIdleLimit;
    private final int _maxWaitWhenFull;
    
    public ConnectionPoolConfig() {
        this(getProperties());
    }
    
    public ConnectionPoolConfig(final Properties properties) {
        this(Integer.parseInt(properties.getProperty("CONNPOOL_MAX_LIMIT")), Integer.parseInt(properties.getProperty("CONNPOOL_MIN_LIMIT")), Integer.parseInt(properties.getProperty("CONNPOOL_MAX_WAIT")));
    }
    
    public ConnectionPoolConfig(final int maxActiveLimit, final int minIdleLimit, final int maxWaitWhenFullMS) {
        this._maxActiveLimit = maxActiveLimit;
        this._minIdleLimit = minIdleLimit;
        this._maxWaitWhenFull = maxWaitWhenFullMS;
        ConnectionPoolConfig._logger.fine("ConnectionPool Configuration Initialized");
        ConnectionPoolConfig._logger.fine("MaxActiveLimit = " + this._maxActiveLimit);
        ConnectionPoolConfig._logger.fine("MinIdleLimit = " + this._minIdleLimit);
        ConnectionPoolConfig._logger.fine("MaxWaitWhenFull = " + this._maxWaitWhenFull);
    }
    
    public int getMaxActiveLimit() {
        return this._maxActiveLimit;
    }
    
    public int getMinIdleLimit() {
        return this._minIdleLimit;
    }
    
    public int getMaxWaitWhenFullMS() {
        return this._maxWaitWhenFull;
    }
    
    public int getMaxWaitWhenFullSec() {
        final int sec = (int)Math.ceil(this._maxWaitWhenFull / 1000.0);
        return sec;
    }
    
    private static Properties getProperties() {
        final Properties properties = new Properties();
        try {
            final PropertySettingProvider connectionPoolSettings = new PropertySettingProvider("com.vontu.util.jdbc.protectconnectionpool.properties.uri");
            properties.putAll(connectionPoolSettings.getPropertyMap());
        }
        catch (Exception e) {
            throw new RuntimeException("Error initializing Connection Pool", e);
        }
        return properties;
    }
    
    static {
        _logger = Logger.getLogger(ConnectionPoolConfig.class.getName());
    }
}
