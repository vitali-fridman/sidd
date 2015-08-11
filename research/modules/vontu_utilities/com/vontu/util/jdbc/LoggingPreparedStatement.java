// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Calendar;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import com.vontu.util.Stopwatch;
import java.util.logging.Level;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class LoggingPreparedStatement extends LoggingStatement implements PreparedStatement
{
    private PreparedStatement preparedStatement;
    private String sql;
    
    public LoggingPreparedStatement(final String sql, final PreparedStatement statement, final JDBCLogger logger) {
        super(statement, logger);
        this.preparedStatement = statement;
        this.sql = sql;
    }
    
    @Override
    public Statement getDelegateStatement() {
        return this.preparedStatement;
    }
    
    @Override
    public void addBatch() throws SQLException {
        this.preparedStatement.addBatch();
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.preparedStatement.clearParameters();
    }
    
    @Override
    public boolean execute() throws SQLException {
        if (this._logger.isLoggable(Level.FINE)) {
            try {
                final int queryNumber = this.incrementStatementNumber();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateStatementBeginMessage(this.sql, queryNumber));
                this._stopWatch.start();
                final boolean result = this.preparedStatement.execute();
                final Stopwatch.Statistics statistics = this._stopWatch.stop();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateStatementEndMessage(statistics, queryNumber));
                return result;
            }
            catch (SQLException e) {
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateFailedMessage(this.sql, e));
                throw e;
            }
        }
        return this.preparedStatement.execute();
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        if (this._logger.isLoggable(Level.FINE)) {
            try {
                final int queryNumber = this.incrementStatementNumber();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateQueryBeginMessage(this.sql, queryNumber));
                this._stopWatch.start();
                final ResultSet result = new LoggingResultSet(this.preparedStatement.executeQuery(), queryNumber, this._logger);
                final Stopwatch.Statistics statistics = this._stopWatch.stop();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateQueryEndMessage(statistics, queryNumber));
                return result;
            }
            catch (SQLException e) {
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateFailedMessage(this.sql, e));
                throw e;
            }
        }
        return this.preparedStatement.executeQuery();
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        if (this._logger.isLoggable(Level.FINE)) {
            try {
                final int queryNumber = this.incrementStatementNumber();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateUpdateBeginMessage(this.sql, queryNumber));
                this._stopWatch.start();
                final int result = this.preparedStatement.executeUpdate();
                final Stopwatch.Statistics statistics = this._stopWatch.stop();
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateUpdateEndMessage(statistics, queryNumber));
                return result;
            }
            catch (SQLException e) {
                this._logger.log(Level.FINE, StatementLoggingFormatter.generateFailedMessage(this.sql, e));
                throw e;
            }
        }
        return this.preparedStatement.executeUpdate();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.preparedStatement.getMetaData();
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.preparedStatement.getParameterMetaData();
    }
    
    @Override
    public void setArray(final int i, final Array x) throws SQLException {
        this.preparedStatement.setArray(i, x);
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x);
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x, length);
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        this.preparedStatement.setAsciiStream(parameterIndex, x, length);
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        this.preparedStatement.setBigDecimal(parameterIndex, x);
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x);
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x, length);
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        this.preparedStatement.setBinaryStream(parameterIndex, x, length);
    }
    
    @Override
    public void setBlob(final int i, final Blob x) throws SQLException {
        this.preparedStatement.setBlob(i, x);
    }
    
    @Override
    public void setBlob(final int i, final InputStream x) throws SQLException {
        this.preparedStatement.setBlob(i, x);
    }
    
    @Override
    public void setBlob(final int i, final InputStream x, final long length) throws SQLException {
        this.preparedStatement.setBlob(i, x, length);
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.preparedStatement.setBoolean(parameterIndex, x);
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.preparedStatement.setByte(parameterIndex, x);
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        this.preparedStatement.setBytes(parameterIndex, x);
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.preparedStatement.setCharacterStream(parameterIndex, reader);
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.preparedStatement.setNCharacterStream(parameterIndex, reader, length);
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.preparedStatement.setNCharacterStream(parameterIndex, reader);
    }
    
    @Override
    public void setClob(final int i, final Clob x) throws SQLException {
        this.preparedStatement.setClob(i, x);
    }
    
    @Override
    public void setClob(final int i, final Reader x) throws SQLException {
        this.preparedStatement.setClob(i, x);
    }
    
    @Override
    public void setClob(final int i, final Reader x, final long length) throws SQLException {
        this.preparedStatement.setClob(i, x, length);
    }
    
    @Override
    public void setNClob(final int i, final NClob x) throws SQLException {
        this.preparedStatement.setNClob(i, x);
    }
    
    @Override
    public void setNClob(final int i, final Reader x) throws SQLException {
        this.preparedStatement.setNClob(i, x);
    }
    
    @Override
    public void setNClob(final int i, final Reader x, final long length) throws SQLException {
        this.preparedStatement.setNClob(i, x, length);
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.preparedStatement.setDate(parameterIndex, x);
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        this.preparedStatement.setDate(parameterIndex, x, cal);
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.preparedStatement.setDouble(parameterIndex, x);
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.preparedStatement.setFloat(parameterIndex, x);
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.preparedStatement.setInt(parameterIndex, x);
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.preparedStatement.setLong(parameterIndex, x);
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.preparedStatement.setNull(parameterIndex, sqlType);
    }
    
    @Override
    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.preparedStatement.setNull(paramIndex, sqlType, typeName);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }
    
    @Override
    public void setRef(final int i, final Ref x) throws SQLException {
        this.preparedStatement.setRef(i, x);
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        this.preparedStatement.setRowId(parameterIndex, x);
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.preparedStatement.setShort(parameterIndex, x);
    }
    
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        this.preparedStatement.setString(parameterIndex, x);
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        this.preparedStatement.setSQLXML(parameterIndex, xmlObject);
    }
    
    @Override
    public void setNString(final int parameterIndex, final String x) throws SQLException {
        this.preparedStatement.setNString(parameterIndex, x);
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        this.preparedStatement.setTime(parameterIndex, x);
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        this.preparedStatement.setTime(parameterIndex, x, cal);
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.preparedStatement.setTimestamp(parameterIndex, x);
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        this.preparedStatement.setTimestamp(parameterIndex, x, cal);
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        this.preparedStatement.setURL(parameterIndex, x);
    }
    
    @Deprecated
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }
}
