// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc.i18n;

import java.sql.SQLWarning;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
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
import java.sql.Statement;
import java.sql.ResultSet;

public class LocalizedResultSet implements ResultSet
{
    private ResultSet _resultSet;
    private Statement _statement;
    
    public LocalizedResultSet(final ResultSet rs, final Statement localizedStatement) {
        this._resultSet = rs;
        this._statement = localizedStatement;
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
        this._resultSet.close();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this._resultSet.deleteRow();
    }
    
    @Override
    public int findColumn(final String columnLabel) throws SQLException {
        return this._resultSet.findColumn(columnLabel);
    }
    
    @Override
    public boolean first() throws SQLException {
        return this._resultSet.first();
    }
    
    @Override
    public Array getArray(final int columnIndex) throws SQLException {
        return this._resultSet.getArray(columnIndex);
    }
    
    @Override
    public Array getArray(final String columnLabel) throws SQLException {
        return this._resultSet.getArray(columnLabel);
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return this._resultSet.getAsciiStream(columnIndex);
    }
    
    @Override
    public InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return this._resultSet.getAsciiStream(columnLabel);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return this._resultSet.getBigDecimal(columnIndex);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return this._resultSet.getBigDecimal(columnLabel);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return this._resultSet.getBigDecimal(columnIndex, scale);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return this._resultSet.getBigDecimal(columnLabel, scale);
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return this._resultSet.getBinaryStream(columnIndex);
    }
    
    @Override
    public InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return this._resultSet.getBinaryStream(columnLabel);
    }
    
    @Override
    public Blob getBlob(final int columnIndex) throws SQLException {
        return this._resultSet.getBlob(columnIndex);
    }
    
    @Override
    public Blob getBlob(final String columnLabel) throws SQLException {
        return this._resultSet.getBlob(columnLabel);
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        return this._resultSet.getBoolean(columnIndex);
    }
    
    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        return this._resultSet.getBoolean(columnLabel);
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        return this._resultSet.getByte(columnIndex);
    }
    
    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        return this._resultSet.getByte(columnLabel);
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        return this._resultSet.getBytes(columnIndex);
    }
    
    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        return this._resultSet.getBytes(columnLabel);
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        return this._resultSet.getCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getCharacterStream(final String columnLabel) throws SQLException {
        return this._resultSet.getCharacterStream(columnLabel);
    }
    
    @Override
    public Clob getClob(final int columnIndex) throws SQLException {
        return this._resultSet.getClob(columnIndex);
    }
    
    @Override
    public Clob getClob(final String columnLabel) throws SQLException {
        return this._resultSet.getClob(columnLabel);
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
    public Date getDate(final String columnLabel) throws SQLException {
        return this._resultSet.getDate(columnLabel);
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getDate(columnIndex, cal);
    }
    
    @Override
    public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return this._resultSet.getDate(columnLabel, cal);
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        return this._resultSet.getDouble(columnIndex);
    }
    
    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        return this._resultSet.getDouble(columnLabel);
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
    public float getFloat(final String columnLabel) throws SQLException {
        return this._resultSet.getFloat(columnLabel);
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
    public int getInt(final String columnLabel) throws SQLException {
        return this._resultSet.getInt(columnLabel);
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        return this._resultSet.getLong(columnIndex);
    }
    
    @Override
    public long getLong(final String columnLabel) throws SQLException {
        return this._resultSet.getLong(columnLabel);
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
    public Object getObject(final String columnLabel) throws SQLException {
        return this._resultSet.getObject(columnLabel);
    }
    
    @Override
    public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return this._resultSet.getObject(columnIndex, map);
    }
    
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return this._resultSet.getObject(columnLabel, map);
    }
    
    @Override
    public Ref getRef(final int columnIndex) throws SQLException {
        return this._resultSet.getRef(columnIndex);
    }
    
    @Override
    public Ref getRef(final String columnLabel) throws SQLException {
        return this._resultSet.getRef(columnLabel);
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
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return this._resultSet.getSQLXML(columnIndex);
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return this._resultSet.getSQLXML(columnLabel);
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        return this._resultSet.getShort(columnIndex);
    }
    
    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return this._resultSet.getShort(columnLabel);
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this._statement;
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        return this._resultSet.getString(columnIndex);
    }
    
    @Override
    public String getString(final String columnLabel) throws SQLException {
        return this._resultSet.getString(columnLabel);
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        return this._resultSet.getTime(columnIndex);
    }
    
    @Override
    public Time getTime(final String columnLabel) throws SQLException {
        return this._resultSet.getTime(columnLabel);
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getTime(columnIndex, cal);
    }
    
    @Override
    public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return this._resultSet.getTime(columnLabel, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return this._resultSet.getTimestamp(columnIndex);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return this._resultSet.getTimestamp(columnLabel);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return this._resultSet.getTimestamp(columnIndex, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return this._resultSet.getTimestamp(columnLabel, cal);
    }
    
    @Override
    public int getType() throws SQLException {
        return this._resultSet.getType();
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return this._resultSet.getUnicodeStream(columnIndex);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return this._resultSet.getUnicodeStream(columnLabel);
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        return this._resultSet.getURL(columnIndex);
    }
    
    @Override
    public URL getURL(final String columnLabel) throws SQLException {
        return this._resultSet.getURL(columnLabel);
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
    public boolean last() throws SQLException {
        return this._resultSet.last();
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
        return this._resultSet.next();
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
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        this._resultSet.updateArray(columnIndex, x);
    }
    
    @Override
    public void updateArray(final String columnLabel, final Array x) throws SQLException {
        this._resultSet.updateArray(columnLabel, x);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        this._resultSet.updateAsciiStream(columnLabel, x);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x, length);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateAsciiStream(columnLabel, x, length);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateAsciiStream(columnIndex, x, length);
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
    public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
        this._resultSet.updateBigDecimal(columnLabel, x);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        this._resultSet.updateBinaryStream(columnLabel, x);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x, length);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        this._resultSet.updateBinaryStream(columnLabel, x, length);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this._resultSet.updateBinaryStream(columnIndex, x, length);
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
    public void updateBlob(final String columnLabel, final Blob x) throws SQLException {
        this._resultSet.updateBlob(columnLabel, x);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        this._resultSet.updateBlob(columnIndex, inputStream);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        this._resultSet.updateBlob(columnLabel, inputStream);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        this._resultSet.updateBlob(columnIndex, inputStream, length);
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
    public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
        this._resultSet.updateBoolean(columnLabel, x);
    }
    
    @Override
    public void updateByte(final int columnIndex, final byte x) throws SQLException {
        this._resultSet.updateByte(columnIndex, x);
    }
    
    @Override
    public void updateByte(final String columnLabel, final byte x) throws SQLException {
        this._resultSet.updateByte(columnLabel, x);
    }
    
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        this._resultSet.updateBytes(columnIndex, x);
    }
    
    @Override
    public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
        this._resultSet.updateBytes(columnLabel, x);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
        this._resultSet.updateCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this._resultSet.updateCharacterStream(columnIndex, x, length);
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
    public void updateClob(final String columnLabel, final Clob x) throws SQLException {
        this._resultSet.updateClob(columnLabel, x);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        this._resultSet.updateClob(columnIndex, reader);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateClob(columnLabel, reader);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateClob(columnIndex, reader, length);
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
    public void updateDate(final String columnLabel, final Date x) throws SQLException {
        this._resultSet.updateDate(columnLabel, x);
    }
    
    @Override
    public void updateDouble(final int columnIndex, final double x) throws SQLException {
        this._resultSet.updateDouble(columnIndex, x);
    }
    
    @Override
    public void updateDouble(final String columnLabel, final double x) throws SQLException {
        this._resultSet.updateDouble(columnLabel, x);
    }
    
    @Override
    public void updateFloat(final int columnIndex, final float x) throws SQLException {
        this._resultSet.updateFloat(columnIndex, x);
    }
    
    @Override
    public void updateFloat(final String columnLabel, final float x) throws SQLException {
        this._resultSet.updateFloat(columnLabel, x);
    }
    
    @Override
    public void updateInt(final int columnIndex, final int x) throws SQLException {
        this._resultSet.updateInt(columnIndex, x);
    }
    
    @Override
    public void updateInt(final String columnLabel, final int x) throws SQLException {
        this._resultSet.updateInt(columnLabel, x);
    }
    
    @Override
    public void updateLong(final int columnIndex, final long x) throws SQLException {
        this._resultSet.updateLong(columnIndex, x);
    }
    
    @Override
    public void updateLong(final String columnLabel, final long x) throws SQLException {
        this._resultSet.updateLong(columnLabel, x);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this._resultSet.updateNCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateNCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this._resultSet.updateNCharacterStream(columnIndex, x, length);
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
    public void updateNClob(final String columnLabel, final NClob clob) throws SQLException {
        this._resultSet.updateNClob(columnLabel, clob);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        this._resultSet.updateNClob(columnIndex, reader);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        this._resultSet.updateNClob(columnLabel, reader);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this._resultSet.updateNClob(columnIndex, reader, length);
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
    public void updateNull(final String columnLabel) throws SQLException {
        this._resultSet.updateNull(columnLabel);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x) throws SQLException {
        this._resultSet.updateObject(columnIndex, x);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x) throws SQLException {
        this._resultSet.updateObject(columnLabel, x);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
        this._resultSet.updateObject(columnIndex, x, scaleOrLength);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
        this._resultSet.updateObject(columnLabel, x, scaleOrLength);
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        this._resultSet.updateRef(columnIndex, x);
    }
    
    @Override
    public void updateRef(final String columnLabel, final Ref x) throws SQLException {
        this._resultSet.updateRef(columnLabel, x);
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
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        this._resultSet.updateSQLXML(columnIndex, xmlObject);
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        this._resultSet.updateSQLXML(columnLabel, xmlObject);
    }
    
    @Override
    public void updateShort(final int columnIndex, final short x) throws SQLException {
        this._resultSet.updateShort(columnIndex, x);
    }
    
    @Override
    public void updateShort(final String columnLabel, final short x) throws SQLException {
        this._resultSet.updateShort(columnLabel, x);
    }
    
    @Override
    public void updateString(final int columnIndex, final String x) throws SQLException {
        this._resultSet.updateString(columnIndex, x);
    }
    
    @Override
    public void updateString(final String columnLabel, final String x) throws SQLException {
        this._resultSet.updateString(columnLabel, x);
    }
    
    @Override
    public void updateTime(final int columnIndex, final Time x) throws SQLException {
        this._resultSet.updateTime(columnIndex, x);
    }
    
    @Override
    public void updateTime(final String columnLabel, final Time x) throws SQLException {
        this._resultSet.updateTime(columnLabel, x);
    }
    
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        this._resultSet.updateTimestamp(columnIndex, x);
    }
    
    @Override
    public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
        this._resultSet.updateTimestamp(columnLabel, x);
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this._resultSet.wasNull();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || this._resultSet.isWrapperFor(iface);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return (T)this;
        }
        return this._resultSet.unwrap(iface);
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
