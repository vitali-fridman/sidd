// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.Map;
import com.vontu.util.config.PropertySettingProvider;
import com.vontu.util.config.SystemProperties;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;

public class JDBCConfig
{
    private HashMap<String, JDBCConnection> _jdbcConnections;
    private JDBCConnection _defaultJDBCConnection;
    private JDBCConnection _reportJDBCConnection;
    private Integer _fetchSize;
    private static final String CONNECTIONS = "jdbc.connections";
    private static final String DEFAULT_CONNECTION = "jdbc.connections.default";
    private static final String REPORT_CONNECTION = "jdbc.connections.report";
    private static final String JDBC_DRIVER_PREFIX = "jdbc.driver.";
    private static final String JDBC_USERNAME_PREFIX = "jdbc.username.";
    private static final String JDBC_SUBPROTOCOL_PREFIX = "jdbc.subprotocol.";
    private static final String JDBC_DBALIAS_PREFIX = "jdbc.dbalias.";
    private static final String REPORT_FETCH_SIZE = "jdbc.report.fetch.size";
    public static final String PROPERTIES = "com.vontu.jdbc.config.properties.uri";
    
    private JDBCConfig(final Properties config, final DatabasePasswordProperties authentication) {
        this.initialize(config, authentication);
    }
    
    public JDBCConfig() throws IOException {
        this(load(), new DatabasePasswordProperties());
    }
    
    public JDBCConnection getDefaultConnection() {
        return this._defaultJDBCConnection;
    }
    
    public JDBCConnection getConnection(final String connectionName) {
        return this._jdbcConnections.get(connectionName);
    }
    
    public JDBCConnection getReportConnection() {
        return this._reportJDBCConnection;
    }
    
    public Iterator<String> getConnectionNames() {
        return this._jdbcConnections.keySet().iterator();
    }
    
    public Integer getReportingFetchSize() {
        return this._fetchSize;
    }
    
    private void initialize(final Properties config, final DatabasePasswordProperties authentication) {
        final String connections = config.getProperty("jdbc.connections");
        final StringTokenizer connectionTokenizer = new StringTokenizer(connections, ",");
        this._jdbcConnections = new HashMap<String, JDBCConnection>();
        this._defaultJDBCConnection = null;
        while (connectionTokenizer.hasMoreTokens()) {
            final String connectionName = connectionTokenizer.nextToken().trim();
            final JDBCConnection connection = this.getConnectionByName(connectionName, config, authentication);
            this._jdbcConnections.put(connectionName, connection);
            if (this._defaultJDBCConnection == null) {
                this._defaultJDBCConnection = connection;
            }
        }
        final String defaultConnection = config.getProperty("jdbc.connections.default");
        if (defaultConnection != null) {
            this._defaultJDBCConnection = this._jdbcConnections.get(defaultConnection);
        }
        this._reportJDBCConnection = this.getConnectionByName(config.getProperty("jdbc.connections.report"), config, authentication);
        final String fetchStr = config.getProperty("jdbc.report.fetch.size");
        if (fetchStr != null) {
            try {
                this._fetchSize = new Integer(fetchStr);
                if (this._fetchSize < 0) {
                    this._fetchSize = null;
                }
            }
            catch (NumberFormatException nfe) {
                this._fetchSize = null;
            }
        }
    }
    
    private static Properties load() throws IOException {
        SystemProperties.load();
        final Properties load = new Properties();
        load.putAll(new PropertySettingProvider("com.vontu.jdbc.config.properties.uri").getPropertyMap());
        return load;
    }
    
    private JDBCConnection getConnectionByName(final String connectionName, final Properties config, final DatabasePasswordProperties authentication) {
        final String driver = config.getProperty("jdbc.driver." + connectionName);
        final String username = config.getProperty("jdbc.username." + connectionName);
        final String password = authentication.getDatabasePassword(connectionName);
        final String subProtocol = config.getProperty("jdbc.subprotocol." + connectionName);
        final String DBAlias = config.getProperty("jdbc.dbalias." + connectionName);
        return new JDBCConnection(connectionName, driver, subProtocol, DBAlias, username, password);
    }
}
