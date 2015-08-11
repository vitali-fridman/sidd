// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;
import com.vontu.util.jdbc.i18n.LocalizedConnection;
import java.util.Locale;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class ProtectConnectionPool
{
    private static DataSource _dataSource;
    private static JDBCConfig _jdbcConfig;
    private static String _identifier;
    
    public ProtectConnectionPool(final String identifier) {
        initialize(identifier);
    }
    
    public static synchronized void initialize(final String identifier) {
        if (null != ProtectConnectionPool._dataSource) {
            shutdown();
        }
        try {
            ProtectConnectionPool._identifier = identifier;
            ProtectConnectionPool._jdbcConfig = new JDBCConfig();
            final ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
            ProtectConnectionPool._dataSource = (DataSource)new DefaultDataSource(ProtectConnectionPool._jdbcConfig.getDefaultConnection(), poolConfig, identifier);
        }
        catch (Exception e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    public static synchronized void initialize(final DataSource dataSource, final JDBCConfig jdbcConfig) {
        ProtectConnectionPool._dataSource = dataSource;
        ProtectConnectionPool._jdbcConfig = jdbcConfig;
    }
    
    public static Connection getConnection() {
        try {
            return ProtectConnectionPool._dataSource.getConnection();
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    public static DataSource getNonLoggingDataSource() {
        if (ProtectConnectionPool._dataSource instanceof DefaultDataSource) {
            return ((DefaultDataSource)ProtectConnectionPool._dataSource).getNonLoggingDataSource();
        }
        return ProtectConnectionPool._dataSource;
    }
    
    public static DataSource getLoggingDataSource() {
        return ProtectConnectionPool._dataSource;
    }
    
    public static Connection getConnection(final Locale locale) {
        try {
            final Connection conn = getConnection();
            return new LocalizedConnection(conn, locale);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
    
    @Deprecated
    public static JDBCConfig getJDBCConfig() {
        return ProtectConnectionPool._jdbcConfig;
    }
    
    public static void shutdown() {
        if (ProtectConnectionPool._dataSource != null && OracleDataSource.class.isAssignableFrom(ProtectConnectionPool._dataSource.getClass())) {
            try {
                ((OracleDataSource)ProtectConnectionPool._dataSource).close();
            }
            catch (SQLException e) {
                throw new DatabaseRuntimeException(e);
            }
        }
    }
    
    public static ConnectionPoolStatisticsMXBean getStatisticsMBean() {
        try {
            return new ConnectionPoolStatistics(OracleConnectionCacheManager.getConnectionCacheManagerInstance(), (OracleDataSource)ProtectConnectionPool._dataSource, ProtectConnectionPool._identifier);
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
}
