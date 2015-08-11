// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;

public class JDBCUtility
{
    public static void cleanUpJDBC(final Connection connection) {
        cleanUpJDBC(connection, null, null);
    }
    
    public static void cleanUpJDBC(final Connection connection, final Statement statement, final ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseRuntimeException(e);
        }
    }
}
