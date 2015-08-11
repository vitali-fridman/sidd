// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc.i18n;

import java.util.concurrent.Executor;
import java.sql.SQLClientInfoException;
import java.sql.Savepoint;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.util.Map;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.dbutils.DbUtils;
import java.util.logging.Level;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import oracle.i18n.util.LocaleMapper;
import java.util.Locale;
import java.util.logging.Logger;
import java.sql.Connection;

public class LocalizedConnection implements Connection
{
    private static Logger _logger;
    private static String _nlsSortSql;
    private static String BINARY;
    private Connection _connection;
    private String _previousNlsSetting;
    
    public LocalizedConnection(final Connection connection, final Locale locale) throws SQLException {
        this._previousNlsSetting = null;
        this._connection = connection;
        String oracleLocale = LocaleMapper.getOraLinguisticSortFromLocale(locale);
        if (locale.equals(Locale.US)) {
            oracleLocale = LocalizedConnection.BINARY;
        }
        if (oracleLocale != null) {
            this._previousNlsSetting = this.getNlsSortSetting();
            if (!oracleLocale.equals(this._previousNlsSetting)) {
                LocalizedConnection._logger.fine("Changing session parameter NLS_SORT from " + this._previousNlsSetting + " to " + oracleLocale);
                final PreparedStatement statement = this._connection.prepareStatement(LocalizedConnection._nlsSortSql + oracleLocale);
                statement.execute();
                statement.close();
            }
        }
    }
    
    private String getNlsSortSetting() {
        Statement stmt = null;
        ResultSet rs = null;
        String nlsSort = null;
        try {
            stmt = this._connection.createStatement();
            rs = stmt.executeQuery("SELECT value FROM nls_session_parameters WHERE parameter = 'NLS_SORT'");
            rs.next();
            nlsSort = rs.getString("value");
        }
        catch (SQLException e) {
            LocalizedConnection._logger.log(Level.WARNING, "Unable to obtain nls sort setting", e);
        }
        finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
        }
        return nlsSort;
    }
    
    @Override
    public void close() throws SQLException {
        try {
            final PreparedStatement statement = this._connection.prepareStatement(LocalizedConnection._nlsSortSql + this._previousNlsSetting);
            statement.execute();
            statement.close();
            LocalizedConnection._logger.fine("reset session NLS_SORT to " + this._previousNlsSetting);
        }
        finally {
            this._connection.close();
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this._connection.clearWarnings();
    }
    
    @Override
    public void commit() throws SQLException {
        this._connection.commit();
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return this._connection.createArrayOf(typeName, elements);
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return this._connection.createBlob();
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return this._connection.createClob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return this._connection.createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this._connection.createSQLXML();
    }
    
    @Override
    public LocalizedStatement createStatement() throws SQLException {
        final Statement s = this._connection.createStatement();
        return new LocalizedStatement(s, this);
    }
    
    @Override
    public LocalizedStatement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        final Statement s = this._connection.createStatement(resultSetType, resultSetConcurrency);
        return new LocalizedStatement(s, this);
    }
    
    @Override
    public LocalizedStatement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final Statement s = this._connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        return new LocalizedStatement(s, this);
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return this._connection.createStruct(typeName, attributes);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return this._connection.getAutoCommit();
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return this._connection.getCatalog();
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return this._connection.getClientInfo();
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return this._connection.getClientInfo(name);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return this._connection.getHoldability();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this._connection.getMetaData();
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return this._connection.getTransactionIsolation();
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this._connection.getTypeMap();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this._connection.getWarnings();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this._connection.isClosed();
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return this._connection.isReadOnly();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return this._connection.isValid(timeout);
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return this._connection.nativeSQL(sql);
    }
    
    @Override
    public LocalizedCallableStatement prepareCall(final String sql) throws SQLException {
        final CallableStatement cs = this._connection.prepareCall(sql);
        return new LocalizedCallableStatement(cs, this);
    }
    
    @Override
    public LocalizedCallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        final CallableStatement cs = this._connection.prepareCall(sql, resultSetType, resultSetConcurrency);
        return new LocalizedCallableStatement(cs, this);
    }
    
    @Override
    public LocalizedCallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final CallableStatement cs = this._connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        return new LocalizedCallableStatement(cs, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql, autoGeneratedKeys);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql, columnIndexes);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql, columnNames);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public LocalizedPreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final PreparedStatement ps = this._connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        return new LocalizedPreparedStatement(ps, this);
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this._connection.releaseSavepoint(savepoint);
    }
    
    @Override
    public void rollback() throws SQLException {
        this._connection.rollback();
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this._connection.rollback(savepoint);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this._connection.setAutoCommit(autoCommit);
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this._connection.setCatalog(catalog);
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        this._connection.setClientInfo(properties);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this._connection.setClientInfo(name, value);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this._connection.setHoldability(holdability);
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this._connection.setReadOnly(readOnly);
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this._connection.setSavepoint();
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return this._connection.setSavepoint(name);
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this._connection.setTransactionIsolation(level);
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this._connection.setTypeMap(map);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || this._connection.isWrapperFor(iface);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return (T)this;
        }
        return this._connection.unwrap(iface);
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        this._connection.setSchema(schema);
    }
    
    @Override
    public String getSchema() throws SQLException {
        return this._connection.getSchema();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        this._connection.abort(executor);
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        this._connection.setNetworkTimeout(executor, milliseconds);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return this._connection.getNetworkTimeout();
    }
    
    static {
        LocalizedConnection._logger = Logger.getLogger(LocalizedConnection.class.getName());
        LocalizedConnection._nlsSortSql = "ALTER SESSION SET NLS_SORT = ";
        LocalizedConnection.BINARY = "BINARY";
    }
}
