// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.Properties;
import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionCacheManager;

public class ConnectionPoolStatistics implements ConnectionPoolStatisticsMXBean
{
    private final String _identifier;
    private final OracleConnectionCacheManager _cacheManager;
    private final OracleDataSource _dataSource;
    
    public ConnectionPoolStatistics(final OracleConnectionCacheManager cacheManager, final OracleDataSource dataSource, final String identifier) {
        this._cacheManager = cacheManager;
        this._dataSource = dataSource;
        this._identifier = identifier;
    }
    
    @Override
    public int getActiveConnectionCount() {
        try {
            return this._cacheManager.getNumberOfActiveConnections(this._identifier);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Override
    public int getIdleConnectionCount() {
        try {
            return this._cacheManager.getNumberOfAvailableConnections(this._identifier);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Override
    public int getMaxAllowedActiveConnections() {
        try {
            final Properties cacheProps = this._dataSource.getConnectionCacheProperties();
            return Integer.parseInt(cacheProps.getProperty("MaxLimit", "0"));
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Override
    public int getMaxIdleConnections() {
        try {
            final Properties cacheProps = this._dataSource.getConnectionCacheProperties();
            return Integer.parseInt(cacheProps.getProperty("MinLimit", "0"));
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Override
    public void setMaxAllowedActiveConnections(final int maxActive) {
        try {
            final Properties cacheProps = this._dataSource.getConnectionCacheProperties();
            cacheProps.setProperty("MaxLimit", Integer.toString(maxActive));
            this._dataSource.setConnectionCacheProperties(cacheProps);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Override
    public void setMaxIdleConnections(final int idle) {
        try {
            final Properties cacheProps = this._dataSource.getConnectionCacheProperties();
            cacheProps.setProperty("MinLimit", Integer.toString(idle));
            this._dataSource.setConnectionCacheProperties(cacheProps);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
}
