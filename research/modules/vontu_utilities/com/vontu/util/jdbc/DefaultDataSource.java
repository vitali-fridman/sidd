// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.Hashtable;
import java.sql.SQLFeatureNotSupportedException;
import java.io.PrintWriter;
import oracle.jdbc.OracleConnection;
import java.sql.Connection;
import java.util.Properties;
import oracle.jdbc.pool.OracleConnectionCacheManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

public class DefaultDataSource extends OracleDataSource
{
    private static final Logger _logger;
    private final String _identifier;
    private final DataSource _nonLoggingDataSource;
    
    public DefaultDataSource(final JDBCConnection jdbcConnection, final String identifier) throws SQLException {
        this.setProgramName(this._identifier = identifier);
        this.setConnectionConfig(jdbcConnection);
        this._nonLoggingDataSource = new NonLoggingDataSource();
    }
    
    public DefaultDataSource(final JDBCConnection jdbcConnection, final ConnectionPoolConfig poolConfig, final String identifier) throws SQLException {
        this._identifier = identifier;
        final OracleConnectionCacheManager cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
        if (cacheManager.existsCache(identifier)) {
            cacheManager.removeCache(identifier, -1L);
            DefaultDataSource._logger.warning("Cache " + identifier + " initialized more than once.  Removed from the CacheManager and replaced");
        }
        this.setConnectionCachingEnabled(true);
        this.setProgramName(this._identifier);
        this.setConnectionConfig(jdbcConnection);
        this.setConnectionCacheName(identifier);
        final Properties cacheProps = new Properties();
        cacheProps.setProperty("MinLimit", Integer.toString(poolConfig.getMinIdleLimit()));
        cacheProps.setProperty("MaxLimit", Integer.toString(poolConfig.getMaxActiveLimit()));
        cacheProps.setProperty("ConnectionWaitTimeout", Integer.toString(poolConfig.getMaxWaitWhenFullSec()));
        this.setConnectionCacheProperties(cacheProps);
        this._nonLoggingDataSource = new NonLoggingDataSource();
    }
    
    public DataSource getNonLoggingDataSource() {
        return this._nonLoggingDataSource;
    }
    
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        return this.wrapConnection(connection);
    }
    
    public Connection getConnection(final Properties properties) throws SQLException {
        final Connection connection = super.getConnection(properties);
        return this.wrapConnection(connection);
    }
    
    public Connection getConnection(final String user, final String password) throws SQLException {
        final Connection connection = super.getConnection(user, password);
        return this.wrapConnection(connection);
    }
    
    public Connection getConnection(final String user, final String password, final Properties properties) throws SQLException {
        final Connection connection = super.getConnection(user, password, properties);
        return this.wrapConnection(connection);
    }
    
    private void setConnectionConfig(final JDBCConnection jdbcConnection) {
        this.setURL(jdbcConnection.getURL());
        this.setUser(jdbcConnection.getUsername());
        this.setPassword(jdbcConnection.getPassword());
    }
    
    private void setProgramName(final String programName) throws SQLException {
        final Properties props = new Properties();
        props.put("v$session.program", programName);
        this.setConnectionProperties(props);
    }
    
    private Connection wrapConnection(final Connection connection) {
        if (connection == null) {
            return null;
        }
        if (this.isLoggingConnection(connection)) {
            return connection;
        }
        return new LoggingConnection(connection);
    }
    
    private Connection unwrapConnection(final Connection connection) throws SQLException {
        if (this.isLoggingConnection(connection)) {
            return connection.unwrap(LoggingConnection.class).getDelegate();
        }
        return connection;
    }
    
    private boolean isLoggingConnection(final Connection connection) {
        try {
            if (connection.isWrapperFor(LoggingConnection.class)) {
                return true;
            }
        }
        catch (SQLException ex) {}
        return false;
    }
    
    public static void setSessionInfo(final Connection connection, final String moduleName, final String clientIdentifier, final String actionName) throws SQLException {
        if (connection.isWrapperFor(OracleConnection.class)) {
            final OracleConnection oracleConnection = connection.unwrap(OracleConnection.class);
            final String[] metrics = { actionName, clientIdentifier, null, moduleName };
            oracleConnection.setEndToEndMetrics(metrics, (short)0);
        }
    }
    
    static /* synthetic */ Connection access$101(final DefaultDataSource x0) throws SQLException {
        return x0.getConnection();
    }
    
    static /* synthetic */ Connection access$301(final DefaultDataSource x0, final String x1, final String x2) throws SQLException {
        return x0.getConnection(x1, x2);
    }
    
    static {
        _logger = Logger.getLogger(DefaultDataSource.class.getName());
    }
    
    private class NonLoggingDataSource implements DataSource
    {
        @Override
        public Connection getConnection() throws SQLException {
            final Connection connection = DefaultDataSource.access$101(DefaultDataSource.this);
            return DefaultDataSource.this.unwrapConnection(connection);
        }
        
        @Override
        public Connection getConnection(final String username, final String password) throws SQLException {
            final Connection connection = DefaultDataSource.access$301(DefaultDataSource.this, username, password);
            return DefaultDataSource.this.unwrapConnection(connection);
        }
        
        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return DefaultDataSource.this.getLogWriter();
        }
        
        @Override
        public void setLogWriter(final PrintWriter out) throws SQLException {
            DefaultDataSource.this.setLogWriter(out);
        }
        
        @Override
        public void setLoginTimeout(final int seconds) throws SQLException {
            DefaultDataSource.this.setLoginTimeout(seconds);
        }
        
        @Override
        public int getLoginTimeout() throws SQLException {
            return DefaultDataSource.this.getLoginTimeout();
        }
        
        @Override
        public <T> T unwrap(final Class<T> iface) throws SQLException {
            return (T)DefaultDataSource.this.unwrap((Class)iface);
        }
        
        @Override
        public boolean isWrapperFor(final Class<?> iface) throws SQLException {
            return DefaultDataSource.this.isWrapperFor((Class)iface);
        }
        
        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException();
        }
    }
}
