// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.util.logging.Level;
import java.sql.SQLWarning;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Statement;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLException;
import com.vontu.util.Stopwatch;
import java.sql.ResultSet;

public class LoggingResultSet implements ResultSet
{
    private final JDBCLogger _logger;
    private ResultSet _resultSet;
    private Object _queryId;
    private Stopwatch _stopWatch;
    private boolean _statisticsAlreadyLogged;
    
    public LoggingResultSet(final ResultSet resultSet, final Object queryId, final JDBCLogger logger) {
        this._stopWatch = new Stopwatch("LoggingResultSet");
        this._statisticsAlreadyLogged = false;
        this._resultSet = resultSet;
        this._queryId = queryId;
        this._logger = logger;
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        return this._resultSet.absolute(row);
    }
    
    @Override
    public void afterLast() throws SQLException {
        this._resultSet.afterLast();
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        this._resultSet.beforeFirst();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        this._resultSet.cancelRowUpdates();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this._resultSet.clearWarnings();
    }
    
    @Override
    public void close() throws SQLException {
        this.logStatistics();
        this._resultSet.close();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this._resultSet.deleteRow();
    }
    
    @Override
    public int findColumn(final String columnName) throws SQLException {
        return this._resultSet.findColumn(columnName);
    }
    
    @Override
    public boolean first() throws SQLException {
        return this._resultSet.first();
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        return this._resultSet.getArray(i);
    }
    
    @Override
    public Array getArray(final String colName) throws SQLException {
        return this._resultSet.getArray(colName);
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return this._resultSet.getAsciiStream(columnIndex);
    }
    
    @Override
    public InputStream getAsciiStream(final String columnName) throws SQLException {
        return this._resultSet.getAsciiStream(columnName);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return this._resultSet.getBigDecimal(columnIndex);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return this._resultSet.getBigDecimal(columnIndex, scale);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        return this._resultSet.getBigDecimal(columnName);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName, final int scale) throws SQLException {
        return this._resultSet.getBigDecimal(columnName, scale);
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return this._resultSet.getBinaryStream(columnIndex);
    }
    
    @Override
    public InputStream getBinaryStream(final String columnName) throws SQLException {
        return this._resultSet.getBinaryStream(columnName);
    }
    
    @Override
    public Blob getBlob(final int i) throws SQLException {
        return this._resultSet.getBlob(i);
    }
    
    @Override
    public Blob getBlob(final String colName) throws SQLException {
        return this._resultSet.getBlob(colName);
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        return this._resultSet.getBoolean(columnIndex);
    }
    
    @Override
    public boolean getBoolean(final String columnName) throws SQLException {
        return this._resultSet.getBoolean(columnName);
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        return this._resultSet.getByte(columnIndex);
    }
    
    @Override
    public byte getByte(final String columnName) throws SQLException {
        return this._resultSet.getByte(columnName);
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        return this._resultSet.getBytes(columnIndex);
    }
    
    @Override
    public byte[] getBytes(final String columnName) throws SQLException {
        return this._resultSet.getBytes(columnName);
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        return this._resultSet.getCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getCharacterStream(final String columnName) throws SQLException {
        return this._resultSet.getCharacterStream(columnName);
    }
    
    @Override
    public Clob getClob(final int i) throws SQLException {
        return this._resultSet.getClob(i);
    }
    
    @Override
    public Clob getClob(final String colName) throws SQLException {
        return this._resultSet.getClob(colName);
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        return this._resultSet.getConcurrency();
    }
    
    @Override
    public String getCursorName() throws SQLException {
        return this._resultSet.getCursorName();
    }
    
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        return this._resultSet.getDate(columnIndex);
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getDate(columnIndex, cal);
    }
    
    @Override
    public Date getDate(final String columnName) throws SQLException {
        return this._resultSet.getDate(columnName);
    }
    
    @Override
    public Date getDate(final String columnName, final Calendar cal) throws SQLException {
        return this._resultSet.getDate(columnName, cal);
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        return this._resultSet.getDouble(columnIndex);
    }
    
    @Override
    public double getDouble(final String columnName) throws SQLException {
        return this._resultSet.getDouble(columnName);
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this._resultSet.getFetchDirection();
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this._resultSet.getFetchSize();
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        return this._resultSet.getFloat(columnIndex);
    }
    
    @Override
    public float getFloat(final String columnName) throws SQLException {
        return this._resultSet.getFloat(columnName);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return this._resultSet.getHoldability();
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        return this._resultSet.getInt(columnIndex);
    }
    
    @Override
    public int getInt(final String columnName) throws SQLException {
        return this._resultSet.getInt(columnName);
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        return this._resultSet.getLong(columnIndex);
    }
    
    @Override
    public long getLong(final String columnName) throws SQLException {
        return this._resultSet.getLong(columnName);
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this._resultSet.getMetaData();
    }
    
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return this._resultSet.getNCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return this._resultSet.getNCharacterStream(columnLabel);
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        return this._resultSet.getNClob(columnIndex);
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        return this._resultSet.getNClob(columnLabel);
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        return this._resultSet.getNString(columnIndex);
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        return this._resultSet.getNString(columnLabel);
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        return this._resultSet.getObject(columnIndex);
    }
    
    @Override
    public Object getObject(final int i, final Map<String, Class<?>> map) throws SQLException {
        return this._resultSet.getObject(i, map);
    }
    
    @Override
    public Object getObject(final String columnName) throws SQLException {
        return this._resultSet.getObject(columnName);
    }
    
    @Override
    public Object getObject(final String colName, final Map<String, Class<?>> map) throws SQLException {
        return this._resultSet.getObject(colName, map);
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        return this._resultSet.getRef(i);
    }
    
    @Override
    public Ref getRef(final String colName) throws SQLException {
        return this._resultSet.getRef(colName);
    }
    
    @Override
    public int getRow() throws SQLException {
        return this._resultSet.getRow();
    }
    
    @Override
    public RowId getRowId(final int columnIndex) throws SQLException {
        return this._resultSet.getRowId(columnIndex);
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        return this._resultSet.getRowId(columnLabel);
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        return this._resultSet.getShort(columnIndex);
    }
    
    @Override
    public short getShort(final String columnName) throws SQLException {
        return this._resultSet.getShort(columnName);
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return this._resultSet.getSQLXML(columnIndex);
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return this._resultSet.getSQLXML(columnLabel);
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this._resultSet.getStatement();
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        return this._resultSet.getString(columnIndex);
    }
    
    @Override
    public String getString(final String columnName) throws SQLException {
        return this._resultSet.getString(columnName);
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        return this._resultSet.getTime(columnIndex);
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getTime(columnIndex, cal);
    }
    
    @Override
    public Time getTime(final String columnName) throws SQLException {
        return this._resultSet.getTime(columnName);
    }
    
    @Override
    public Time getTime(final String columnName, final Calendar cal) throws SQLException {
        return this._resultSet.getTime(columnName, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return this._resultSet.getTimestamp(columnIndex);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getTimestamp(columnIndex, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName) throws SQLException {
        return this._resultSet.getTimestamp(columnName);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName, final Calendar cal) throws SQLException {
        return this._resultSet.getTimestamp(columnName, cal);
    }
    
    @Override
    public int getType() throws SQLException {
        return this._resultSet.getType();
    }
    
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return this._resultSet.getUnicodeStream(columnIndex);
    }
    
    @Override
    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        return this._resultSet.getUnicodeStream(columnName);
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        return this._resultSet.getURL(columnIndex);
    }
    
    @Override
    public URL getURL(final String columnName) throws SQLException {
        return this._resultSet.getURL(columnName);
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this._resultSet.getWarnings();
    }
    
    @Override
    public void insertRow() throws SQLException {
        this._resultSet.insertRow();
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        return this._resultSet.isAfterLast();
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this._resultSet.isBeforeFirst();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this._resultSet.isClosed();
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        return this._resultSet.isFirst();
    }
    
    @Override
    public boolean isLast() throws SQLException {
        return this._resultSet.isLast();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || this._resultSet.isWrapperFor(iface);
    }
    
    @Override
    public boolean last() throws SQLException {
        return this._resultSet.last();
    }
    
    private void logStatistics() {
        if (!this._statisticsAlreadyLogged && this._logger.isLoggable(Level.FINE)) {
            final Stopwatch.Statistics statistics = this._stopWatch.getStatistics();
            if (statistics != null) {
                this._logger.fine("-- Fetch Time (id=" + this._queryId + "). Avg: " + statistics.getAverageTime() + " ms. Min: " + statistics.getMinTime() + " ms. Max: " + statistics.getMaxTime() + " ms. Total: " + statistics.getTotalTime() + " ms. Rows requested: " + statistics.getCount());
            }
        }
        this._statisticsAlreadyLogged = true;
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        this._resultSet.moveToCurrentRow();
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        this._resultSet.moveToInsertRow();
    }
    
    @Override
    public boolean next() throws SQLException {
        if (this._logger.isLoggable(Level.FINE) && !this._statisticsAlreadyLogged) {
            this._stopWatch.start();
        }
        final boolean result = this._resultSet.next();
        if (this._logger.isLoggable(Level.FINE) && !this._statisticsAlreadyLogged) {
            this._stopWatch.stop();
        }
        if (!result) {
            this.logStatistics();
        }
        return result;
    }
    
    @Override
    public boolean previous() throws SQLException {
        return this._resultSet.previous();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        this._resultSet.refreshRow();
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        return this._resultSet.relative(rows);
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        return this._resultSet.rowDeleted();
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        return this._resultSet.rowInserted();
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        return this._resultSet.rowUpdated();
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this._resultSet.setFetchDirection(direction);
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this._resultSet.setFetchSize(rows);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return (T)this;
        }
        return this._resultSet.unwrap(iface);
    }
    
    @Override
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        this._resultSet.updateArray(columnIndex, x);
    }
    
    @Override
    public void updateArray(final String columnName, final Array x) throws SQLException {
        this._resultSet.updateArray(columnName, x);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x, length);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x, length);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        this._resultSet.updateAsciiStream(columnLabel, x);
    }
    
    @Override
    public void updateAsciiStream(final String columnName, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateAsciiStream(columnName, x, length);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateAsciiStream(columnLabel, x, length);
    }
    
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        this._resultSet.updateBigDecimal(columnIndex, x);
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x) throws SQLException {
        this._resultSet.updateBigDecimal(columnName, x);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x, length);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x, length);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        this._resultSet.updateBinaryStream(columnLabel, x);
    }
    
    @Override
    public void updateBinaryStream(final String columnName, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateBinaryStream(columnName, x, length);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateBinaryStream(columnLabel, x, length);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        this._resultSet.updateBlob(columnIndex, x);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        this._resultSet.updateBlob(columnIndex, inputStream);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        this._resultSet.updateBlob(columnIndex, inputStream, length);
    }
    
    @Override
    public void updateBlob(final String columnName, final Blob x) throws SQLException {
        this._resultSet.updateBlob(columnName, x);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        this._resultSet.updateBlob(columnLabel, inputStream);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        this._resultSet.updateBlob(columnLabel, inputStream, length);
    }
    
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        this._resultSet.updateBoolean(columnIndex, x);
    }
    
    @Override
    public void updateBoolean(final String columnName, final boolean x) throws SQLException {
        this._resultSet.updateBoolean(columnName, x);
    }
    
    @Override
    public void updateByte(final int columnIndex, final byte x) throws SQLException {
        this._resultSet.updateByte(columnIndex, x);
    }
    
    @Override
    public void updateByte(final String columnName, final byte x) throws SQLException {
        this._resultSet.updateByte(columnName, x);
    }
    
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        this._resultSet.updateBytes(columnIndex, x);
    }
    
    @Override
    public void updateBytes(final String columnName, final byte[] x) throws SQLException {
        this._resultSet.updateBytes(columnName, x);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateCharacterStream(final String columnName, final Reader reader, final int length) throws SQLException {
        this._resultSet.updateCharacterStream(columnName, reader, length);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Clob x) throws SQLException {
        this._resultSet.updateClob(columnIndex, x);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        this._resultSet.updateClob(columnIndex, reader);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateClob(columnIndex, reader, length);
    }
    
    @Override
    public void updateClob(final String columnName, final Clob x) throws SQLException {
        this._resultSet.updateClob(columnName, x);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateClob(columnLabel, reader);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateClob(columnLabel, reader, length);
    }
    
    @Override
    public void updateDate(final int columnIndex, final Date x) throws SQLException {
        this._resultSet.updateDate(columnIndex, x);
    }
    
    @Override
    public void updateDate(final String columnName, final Date x) throws SQLException {
        this._resultSet.updateDate(columnName, x);
    }
    
    @Override
    public void updateDouble(final int columnIndex, final double x) throws SQLException {
        this._resultSet.updateDouble(columnIndex, x);
    }
    
    @Override
    public void updateDouble(final String columnName, final double x) throws SQLException {
        this._resultSet.updateDouble(columnName, x);
    }
    
    @Override
    public void updateFloat(final int columnIndex, final float x) throws SQLException {
        this._resultSet.updateFloat(columnIndex, x);
    }
    
    @Override
    public void updateFloat(final String columnName, final float x) throws SQLException {
        this._resultSet.updateFloat(columnName, x);
    }
    
    @Override
    public void updateInt(final int columnIndex, final int x) throws SQLException {
        this._resultSet.updateInt(columnIndex, x);
    }
    
    @Override
    public void updateInt(final String columnName, final int x) throws SQLException {
        this._resultSet.updateInt(columnName, x);
    }
    
    @Override
    public void updateLong(final int columnIndex, final long x) throws SQLException {
        this._resultSet.updateLong(columnIndex, x);
    }
    
    @Override
    public void updateLong(final String columnName, final long x) throws SQLException {
        this._resultSet.updateLong(columnName, x);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this._resultSet.updateNCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this._resultSet.updateNCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateNCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateNCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob clob) throws SQLException {
        this._resultSet.updateNClob(columnIndex, clob);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        this._resultSet.updateNClob(columnIndex, reader);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateNClob(columnIndex, reader, length);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob clob) throws SQLException {
        this._resultSet.updateNClob(columnLabel, clob);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateNClob(columnLabel, reader);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateNClob(columnLabel, reader, length);
    }
    
    @Override
    public void updateNString(final int columnIndex, final String string) throws SQLException {
        this._resultSet.updateNString(columnIndex, string);
    }
    
    @Override
    public void updateNString(final String columnLabel, final String string) throws SQLException {
        this._resultSet.updateNString(columnLabel, string);
    }
    
    @Override
    public void updateNull(final int columnIndex) throws SQLException {
        this._resultSet.updateNull(columnIndex);
    }
    
    @Override
    public void updateNull(final String columnName) throws SQLException {
        this._resultSet.updateNull(columnName);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x) throws SQLException {
        this._resultSet.updateObject(columnIndex, x);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scale) throws SQLException {
        this._resultSet.updateObject(columnIndex, x, scale);
    }
    
    @Override
    public void updateObject(final String columnName, final Object x) throws SQLException {
        this._resultSet.updateObject(columnName, x);
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int scale) throws SQLException {
        this._resultSet.updateObject(columnName, x, scale);
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        this._resultSet.updateRef(columnIndex, x);
    }
    
    @Override
    public void updateRef(final String columnName, final Ref x) throws SQLException {
        this._resultSet.updateRef(columnName, x);
    }
    
    @Override
    public void updateRow() throws SQLException {
        this._resultSet.updateRow();
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        this._resultSet.updateRowId(columnIndex, x);
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        this._resultSet.updateRowId(columnLabel, x);
    }
    
    @Override
    public void updateShort(final int columnIndex, final short x) throws SQLException {
        this._resultSet.updateShort(columnIndex, x);
    }
    
    @Override
    public void updateShort(final String columnName, final short x) throws SQLException {
        this._resultSet.updateShort(columnName, x);
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        this._resultSet.updateSQLXML(columnIndex, xmlObject);
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        this._resultSet.updateSQLXML(columnLabel, xmlObject);
    }
    
    @Override
    public void updateString(final int columnIndex, final String x) throws SQLException {
        this._resultSet.updateString(columnIndex, x);
    }
    
    @Override
    public void updateString(final String columnName, final String x) throws SQLException {
        this._resultSet.updateString(columnName, x);
    }
    
    @Override
    public void updateTime(final int columnIndex, final Time x) throws SQLException {
        this._resultSet.updateTime(columnIndex, x);
    }
    
    @Override
    public void updateTime(final String columnName, final Time x) throws SQLException {
        this._resultSet.updateTime(columnName, x);
    }
    
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        this._resultSet.updateTimestamp(columnIndex, x);
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x) throws SQLException {
        this._resultSet.updateTimestamp(columnName, x);
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this._resultSet.wasNull();
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return this._resultSet.getObject(columnIndex, type);
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        return this._resultSet.getObject(columnLabel, type);
    }
}
