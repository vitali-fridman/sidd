// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class JDBCConnection
{
    private String _connectionName;
    private String _username;
    private String _password;
    private String _subProtocol;
    private String _DBAlias;
    private String _driverClass;
    private int _hashCode;
    
    public JDBCConnection(final String connectionName, final String driverClass, final String subProtocol, final String DBAlias, final String username, final String password) {
        this._connectionName = null;
        this._username = null;
        this._password = null;
        this._subProtocol = null;
        this._DBAlias = null;
        this._driverClass = null;
        assert connectionName != null;
        assert driverClass != null;
        assert subProtocol != null;
        assert DBAlias != null;
        assert username != null;
        assert password != null;
        this._connectionName = connectionName;
        this._driverClass = driverClass;
        this._subProtocol = subProtocol;
        this._DBAlias = DBAlias;
        this._username = username;
        this._password = password;
        final StringBuffer hashCodeString = new StringBuffer();
        hashCodeString.append(this._subProtocol);
        hashCodeString.append(this._DBAlias);
        hashCodeString.append(this._username);
        hashCodeString.append(this._password);
        this._hashCode = hashCodeString.toString().hashCode();
    }
    
    public String getConnectionName() {
        return this._connectionName;
    }
    
    public String getDriverClass() {
        return this._driverClass;
    }
    
    public String getUsername() {
        return this._username;
    }
    
    public String getPassword() {
        return this._password;
    }
    
    public String getSubProtocol() {
        return this._subProtocol;
    }
    
    public String getDBAlias() {
        return this._DBAlias;
    }
    
    public String getURL() {
        return "jdbc:" + this._subProtocol + ":" + this._DBAlias;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean isEqual = false;
        if (this == obj) {
            return true;
        }
        if (obj instanceof JDBCConnection) {
            final JDBCConnection connection = (JDBCConnection)obj;
            if (this._subProtocol.equals(connection.getSubProtocol()) && this._DBAlias.equals(connection.getDBAlias()) && this._username.equals(connection.getUsername()) && this._password.equals(connection.getPassword())) {
                isEqual = true;
            }
        }
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    public boolean testConnection() {
        boolean activeConnection = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            connection = DriverManager.getConnection(this.getURL(), this.getUsername(), this.getPassword());
            statement = connection.createStatement();
            result = statement.executeQuery("select 1 from dual");
            activeConnection = true;
        }
        catch (Throwable t) {}
        finally {
            if (result != null) {
                try {
                    result.close();
                }
                catch (SQLException ex) {}
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException ex2) {}
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException ex3) {}
            }
        }
        return activeConnection;
    }
}
