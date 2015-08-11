// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCTestConnection
{
    private static Logger _logger;
    private static String _testSQL;
    
    public static boolean isDefaultConnectionUp() {
        JDBCConfig jdbcConfig = null;
        JDBCConnection defaultConnection = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            jdbcConfig = new JDBCConfig();
        }
        catch (Exception e) {
            JDBCTestConnection._logger.log(Level.SEVERE, "Invalid JDBC configuration file: " + e.getMessage());
        }
        try {
            defaultConnection = jdbcConfig.getDefaultConnection();
            conn = DatabaseConnectionFactory.createConnection(defaultConnection, "Vontu_JDBC_Test");
            stmt = conn.createStatement();
            stmt.execute(JDBCTestConnection._testSQL);
            return true;
        }
        catch (Exception e) {
            if (JDBCTestConnection._logger.isLoggable(Level.FINE)) {
                JDBCTestConnection._logger.log(Level.FINE, "Cannot connect to database: " + e.getMessage());
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception ex) {}
        }
        return false;
    }
    
    public static boolean waitDefaultDBConnection(final int numRetry, final int interval) {
        if (numRetry <= 0) {
            throw new IllegalArgumentException("numRetry must be greater than 0");
        }
        for (int i = 0; i < numRetry; ++i) {
            if (isDefaultConnectionUp()) {
                return true;
            }
            try {
                Thread.sleep(interval * 1000);
            }
            catch (InterruptedException ex) {}
        }
        return false;
    }
    
    static {
        JDBCTestConnection._logger = Logger.getLogger(JDBCTestConnection.class.getName());
        JDBCTestConnection._testSQL = "select * from dual";
    }
}
