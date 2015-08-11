// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import java.sql.NClob;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;

public class LoggingCallableStatement extends LoggingPreparedStatement implements CallableStatement
{
    private CallableStatement _callableStatement;
    
    public LoggingCallableStatement(final String sql, final CallableStatement callableStatement, final JDBCLogger logger) {
        super(sql, callableStatement, logger);
        this._callableStatement = callableStatement;
    }
    
    public void logString(final int parameterIndex) throws SQLException {
        if (this._logger.isLoggable(Level.FINE)) {
            this._logger.fine("Parameter " + parameterIndex + ":\n" + this.getString(parameterIndex));
        }
    }
    
    public void logString(final String parameterName) throws SQLException {
        if (this._logger.isLoggable(Level.FINE)) {
            this._logger.fine("Parameter " + parameterName + ":\n" + this.getString(parameterName));
        }
    }
    
    @Override
    public Array getArray(final int parameterIndex) throws SQLException {
        return this._callableStatement.getArray(parameterIndex);
    }
    
    @Override
    public Array getArray(final String parameterName) throws SQLException {
        return this._callableStatement.getArray(parameterName);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex) throws SQLException {
        return this._callableStatement.getBigDecimal(parameterIndex);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
        return this._callableStatement.getBigDecimal(parameterName);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
        return this._callableStatement.getBigDecimal(parameterIndex, scale);
    }
    
    @Override
    public Blob getBlob(final int parameterIndex) throws SQLException {
        return this._callableStatement.getBlob(parameterIndex);
    }
    
    @Override
    public Blob getBlob(final String parameterName) throws SQLException {
        return this._callableStatement.getBlob(parameterName);
    }
    
    @Override
    public boolean getBoolean(final int parameterIndex) throws SQLException {
        return this._callableStatement.getBoolean(parameterIndex);
    }
    
    @Override
    public boolean getBoolean(final String parameterName) throws SQLException {
        return this._callableStatement.getBoolean(parameterName);
    }
    
    @Override
    public byte getByte(final int parameterIndex) throws SQLException {
        return this._callableStatement.getByte(parameterIndex);
    }
    
    @Override
    public byte getByte(final String parameterName) throws SQLException {
        return this._callableStatement.getByte(parameterName);
    }
    
    @Override
    public byte[] getBytes(final int parameterIndex) throws SQLException {
        return this._callableStatement.getBytes(parameterIndex);
    }
    
    @Override
    public byte[] getBytes(final String parameterName) throws SQLException {
        return this._callableStatement.getBytes(parameterName);
    }
    
    @Override
    public Reader getCharacterStream(final int parameterIndex) throws SQLException {
        return this._callableStatement.getCharacterStream(parameterIndex);
    }
    
    @Override
    public Reader getCharacterStream(final String parameterName) throws SQLException {
        return this._callableStatement.getCharacterStream(parameterName);
    }
    
    @Override
    public Clob getClob(final int parameterIndex) throws SQLException {
        return this._callableStatement.getClob(parameterIndex);
    }
    
    @Override
    public Clob getClob(final String parameterName) throws SQLException {
        return this._callableStatement.getClob(parameterName);
    }
    
    @Override
    public Date getDate(final int parameterIndex) throws SQLException {
        return this._callableStatement.getDate(parameterIndex);
    }
    
    @Override
    public Date getDate(final String parameterName) throws SQLException {
        return this._callableStatement.getDate(parameterName);
    }
    
    @Override
    public Date getDate(final int parameterIndex, final Calendar cal) throws SQLException {
        return this._callableStatement.getDate(parameterIndex, cal);
    }
    
    @Override
    public Date getDate(final String parameterName, final Calendar cal) throws SQLException {
        return this._callableStatement.getDate(parameterName, cal);
    }
    
    @Override
    public double getDouble(final int parameterIndex) throws SQLException {
        return this._callableStatement.getDouble(parameterIndex);
    }
    
    @Override
    public double getDouble(final String parameterName) throws SQLException {
        return this._callableStatement.getDouble(parameterName);
    }
    
    @Override
    public float getFloat(final int parameterIndex) throws SQLException {
        return this._callableStatement.getFloat(parameterIndex);
    }
    
    @Override
    public float getFloat(final String parameterName) throws SQLException {
        return this._callableStatement.getFloat(parameterName);
    }
    
    @Override
    public int getInt(final int parameterIndex) throws SQLException {
        return this._callableStatement.getInt(parameterIndex);
    }
    
    @Override
    public int getInt(final String parameterName) throws SQLException {
        return this._callableStatement.getInt(parameterName);
    }
    
    @Override
    public long getLong(final int parameterIndex) throws SQLException {
        return this._callableStatement.getLong(parameterIndex);
    }
    
    @Override
    public long getLong(final String parameterName) throws SQLException {
        return this._callableStatement.getLong(parameterName);
    }
    
    @Override
    public Reader getNCharacterStream(final int parameterIndex) throws SQLException {
        return this._callableStatement.getNCharacterStream(parameterIndex);
    }
    
    @Override
    public Reader getNCharacterStream(final String parameterName) throws SQLException {
        return this._callableStatement.getNCharacterStream(parameterName);
    }
    
    @Override
    public NClob getNClob(final int parameterIndex) throws SQLException {
        return this._callableStatement.getNClob(parameterIndex);
    }
    
    @Override
    public NClob getNClob(final String parameterName) throws SQLException {
        return this._callableStatement.getNClob(parameterName);
    }
    
    @Override
    public String getNString(final int parameterIndex) throws SQLException {
        return this._callableStatement.getNString(parameterIndex);
    }
    
    @Override
    public String getNString(final String parameterName) throws SQLException {
        return this._callableStatement.getNString(parameterName);
    }
    
    @Override
    public Object getObject(final int parameterIndex) throws SQLException {
        return this._callableStatement.getObject(parameterIndex);
    }
    
    @Override
    public Object getObject(final String parameterName) throws SQLException {
        return this._callableStatement.getObject(parameterName);
    }
    
    @Override
    public Object getObject(final int parameterIndex, final Map<String, Class<?>> map) throws SQLException {
        return this._callableStatement.getObject(parameterIndex, map);
    }
    
    @Override
    public Object getObject(final String parameterName, final Map<String, Class<?>> map) throws SQLException {
        return this._callableStatement.getObject(parameterName, map);
    }
    
    @Override
    public Ref getRef(final int parameterIndex) throws SQLException {
        return this._callableStatement.getRef(parameterIndex);
    }
    
    @Override
    public Ref getRef(final String parameterName) throws SQLException {
        return this._callableStatement.getRef(parameterName);
    }
    
    @Override
    public RowId getRowId(final int parameterIndex) throws SQLException {
        return this._callableStatement.getRowId(parameterIndex);
    }
    
    @Override
    public RowId getRowId(final String parameterName) throws SQLException {
        return this._callableStatement.getRowId(parameterName);
    }
    
    @Override
    public SQLXML getSQLXML(final int parameterIndex) throws SQLException {
        return this._callableStatement.getSQLXML(parameterIndex);
    }
    
    @Override
    public SQLXML getSQLXML(final String parameterName) throws SQLException {
        return this._callableStatement.getSQLXML(parameterName);
    }
    
    @Override
    public short getShort(final int parameterIndex) throws SQLException {
        return this._callableStatement.getShort(parameterIndex);
    }
    
    @Override
    public short getShort(final String parameterName) throws SQLException {
        return this._callableStatement.getShort(parameterName);
    }
    
    @Override
    public String getString(final int parameterIndex) throws SQLException {
        return this._callableStatement.getString(parameterIndex);
    }
    
    @Override
    public String getString(final String parameterName) throws SQLException {
        return this._callableStatement.getString(parameterName);
    }
    
    @Override
    public Time getTime(final int parameterIndex) throws SQLException {
        return this._callableStatement.getTime(parameterIndex);
    }
    
    @Override
    public Time getTime(final String parameterName) throws SQLException {
        return this._callableStatement.getTime(parameterName);
    }
    
    @Override
    public Time getTime(final int parameterIndex, final Calendar cal) throws SQLException {
        return this._callableStatement.getTime(parameterIndex, cal);
    }
    
    @Override
    public Time getTime(final String parameterName, final Calendar cal) throws SQLException {
        return this._callableStatement.getTime(parameterName, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final int parameterIndex) throws SQLException {
        return this._callableStatement.getTimestamp(parameterIndex);
    }
    
    @Override
    public Timestamp getTimestamp(final String parameterName) throws SQLException {
        return this._callableStatement.getTimestamp(parameterName);
    }
    
    @Override
    public Timestamp getTimestamp(final int parameterIndex, final Calendar cal) throws SQLException {
        return this._callableStatement.getTimestamp(parameterIndex, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
        return this._callableStatement.getTimestamp(parameterName, cal);
    }
    
    @Override
    public URL getURL(final int parameterIndex) throws SQLException {
        return this._callableStatement.getURL(parameterIndex);
    }
    
    @Override
    public URL getURL(final String parameterName) throws SQLException {
        return this._callableStatement.getURL(parameterName);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final int sqlType) throws SQLException {
        this._callableStatement.registerOutParameter(parameterIndex, sqlType);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
        this._callableStatement.registerOutParameter(parameterName, sqlType);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
        this._callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
        this._callableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
        this._callableStatement.registerOutParameter(parameterName, sqlType, scale);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this._callableStatement.registerOutParameter(parameterName, sqlType, typeName);
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream x) throws SQLException {
        this._callableStatement.setAsciiStream(parameterName, x);
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this._callableStatement.setAsciiStream(parameterName, x, length);
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream x, final long length) throws SQLException {
        this._callableStatement.setAsciiStream(parameterName, x, length);
    }
    
    @Override
    public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
        this._callableStatement.setBigDecimal(parameterName, x);
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream x) throws SQLException {
        this._callableStatement.setBinaryStream(parameterName, x);
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this._callableStatement.setBinaryStream(parameterName, x, length);
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream x, final long length) throws SQLException {
        this._callableStatement.setBinaryStream(parameterName, x, length);
    }
    
    @Override
    public void setBlob(final String parameterName, final Blob x) throws SQLException {
        this._callableStatement.setBlob(parameterName, x);
    }
    
    @Override
    public void setBlob(final String parameterName, final InputStream inputStream) throws SQLException {
        this._callableStatement.setBlob(parameterName, inputStream);
    }
    
    @Override
    public void setBlob(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        this._callableStatement.setBlob(parameterName, inputStream, length);
    }
    
    @Override
    public void setBoolean(final String parameterName, final boolean x) throws SQLException {
        this._callableStatement.setBoolean(parameterName, x);
    }
    
    @Override
    public void setByte(final String parameterName, final byte x) throws SQLException {
        this._callableStatement.setByte(parameterName, x);
    }
    
    @Override
    public void setBytes(final String parameterName, final byte[] x) throws SQLException {
        this._callableStatement.setBytes(parameterName, x);
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader) throws SQLException {
        this._callableStatement.setCharacterStream(parameterName, reader);
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader, final int length) throws SQLException {
        this._callableStatement.setCharacterStream(parameterName, reader, length);
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader, final long length) throws SQLException {
        this._callableStatement.setCharacterStream(parameterName, reader, length);
    }
    
    @Override
    public void setClob(final String parameterName, final Clob x) throws SQLException {
        this._callableStatement.setClob(parameterName, x);
    }
    
    @Override
    public void setClob(final String parameterName, final Reader reader) throws SQLException {
        this._callableStatement.setClob(parameterName, reader);
    }
    
    @Override
    public void setClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this._callableStatement.setClob(parameterName, reader, length);
    }
    
    @Override
    public void setDate(final String parameterName, final Date x) throws SQLException {
        this._callableStatement.setDate(parameterName, x);
    }
    
    @Override
    public void setDate(final String parameterName, final Date x, final Calendar cal) throws SQLException {
        this._callableStatement.setDate(parameterName, x, cal);
    }
    
    @Override
    public void setDouble(final String parameterName, final double x) throws SQLException {
        this._callableStatement.setDouble(parameterName, x);
    }
    
    @Override
    public void setFloat(final String parameterName, final float x) throws SQLException {
        this._callableStatement.setFloat(parameterName, x);
    }
    
    @Override
    public void setInt(final String parameterName, final int x) throws SQLException {
        this._callableStatement.setInt(parameterName, x);
    }
    
    @Override
    public void setLong(final String parameterName, final long x) throws SQLException {
        this._callableStatement.setLong(parameterName, x);
    }
    
    @Override
    public void setNCharacterStream(final String parameterName, final Reader value) throws SQLException {
        this._callableStatement.setNCharacterStream(parameterName, value);
    }
    
    @Override
    public void setNCharacterStream(final String parameterName, final Reader value, final long length) throws SQLException {
        this._callableStatement.setNCharacterStream(parameterName, value, length);
    }
    
    @Override
    public void setNClob(final String parameterName, final NClob value) throws SQLException {
        this._callableStatement.setNClob(parameterName, value);
    }
    
    @Override
    public void setNClob(final String parameterName, final Reader reader) throws SQLException {
        this._callableStatement.setNClob(parameterName, reader);
    }
    
    @Override
    public void setNClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this._callableStatement.setNClob(parameterName, reader, length);
    }
    
    @Override
    public void setNString(final String parameterName, final String value) throws SQLException {
        this._callableStatement.setNString(parameterName, value);
    }
    
    @Override
    public void setNull(final String parameterName, final int sqlType) throws SQLException {
        this._callableStatement.setNull(parameterName, sqlType);
    }
    
    @Override
    public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this._callableStatement.setNull(parameterName, sqlType, typeName);
    }
    
    @Override
    public void setObject(final String parameterName, final Object x) throws SQLException {
        this._callableStatement.setObject(parameterName, x);
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
        this._callableStatement.setObject(parameterName, x, targetSqlType);
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this._callableStatement.setObject(parameterName, x, targetSqlType, scale);
    }
    
    @Override
    public void setRowId(final String parameterName, final RowId x) throws SQLException {
        this._callableStatement.setRowId(parameterName, x);
    }
    
    @Override
    public void setSQLXML(final String parameterName, final SQLXML xmlObject) throws SQLException {
        this._callableStatement.setSQLXML(parameterName, xmlObject);
    }
    
    @Override
    public void setShort(final String parameterName, final short x) throws SQLException {
        this._callableStatement.setShort(parameterName, x);
    }
    
    @Override
    public void setString(final String parameterName, final String x) throws SQLException {
        this._callableStatement.setString(parameterName, x);
    }
    
    @Override
    public void setTime(final String parameterName, final Time x) throws SQLException {
        this._callableStatement.setTime(parameterName, x);
    }
    
    @Override
    public void setTime(final String parameterName, final Time x, final Calendar cal) throws SQLException {
        this._callableStatement.setTime(parameterName, x, cal);
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp x) throws SQLException {
        this._callableStatement.setTimestamp(parameterName, x);
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp x, final Calendar cal) throws SQLException {
        this._callableStatement.setTimestamp(parameterName, x, cal);
    }
    
    @Override
    public void setURL(final String parameterName, final URL val) throws SQLException {
        this._callableStatement.setURL(parameterName, val);
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this._callableStatement.wasNull();
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        this._callableStatement.closeOnCompletion();
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this._callableStatement.isCloseOnCompletion();
    }
    
    @Override
    public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
        return this._callableStatement.getObject(parameterIndex, type);
    }
    
    @Override
    public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
        return (T)this.getObject(parameterName, (Class<Object>)type);
    }
}
