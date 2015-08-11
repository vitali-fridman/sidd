// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;

public class DatabaseConnectionFactory implements ConnectionFactory
{
    private final DataSource _dataSource;
    
    public DatabaseConnectionFactory(final JDBCConnection jdbcConnection, final String identifier) throws SQLException {
        this._dataSource = (DataSource)new DefaultDataSource(jdbcConnection, identifier);
    }
    
    public static Connection createConnection(final JDBCConnection jdbcConnection, final String identifier) throws SQLException {
        final DataSource dataSource = (DataSource)new DefaultDataSource(jdbcConnection, identifier);
        return dataSource.getConnection();
    }
    
    public Connection createConnection() throws SQLException {
        return this._dataSource.getConnection();
    }
}
