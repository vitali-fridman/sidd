// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.jdbc;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;

public class JDBCResultSetTextResolver
{
    int[] _columnTypes;
    String[] _columnNames;
    ResultSet _rslt;
    final boolean _useStandardNotationForNumbers;
    
    public JDBCResultSetTextResolver(final ResultSet rslt) throws SQLException {
        this(rslt, false);
    }
    
    public JDBCResultSetTextResolver(final ResultSet rslt, final boolean useStandardNotationForNumbers) throws SQLException {
        this._rslt = rslt;
        this._columnTypes = this.resolveColumnTypes(rslt.getMetaData());
        this._columnNames = this.resolveColumnNames(rslt.getMetaData());
        this._useStandardNotationForNumbers = useStandardNotationForNumbers;
    }
    
    private int[] resolveColumnTypes(final ResultSetMetaData metadata) throws SQLException {
        final int[] types = new int[metadata.getColumnCount()];
        for (int i = 1; i <= types.length; ++i) {
            types[i - 1] = metadata.getColumnType(i);
        }
        return types;
    }
    
    private String[] resolveColumnNames(final ResultSetMetaData metadata) throws SQLException {
        final String[] names = new String[metadata.getColumnCount()];
        for (int i = 1; i <= names.length; ++i) {
            names[i - 1] = metadata.getColumnName(i);
        }
        return names;
    }
    
    public String getSQLColumnName(final int index) {
        return this._columnNames[index - 1];
    }
    
    public int getSQLColumnType(final int index) {
        return this._columnTypes[index - 1];
    }
    
    public String getSQLColumnTypeName(final int index) {
        final int columnType = this.getSQLColumnType(index);
        switch (columnType) {
            case 2003: {
                return "Array";
            }
            case -5: {
                return "Big Int";
            }
            case -2: {
                return "Binary";
            }
            case -7: {
                return "Bit";
            }
            case 2004: {
                return "BLOB";
            }
            case 16: {
                return "Boolean";
            }
            case 1: {
                return "Char";
            }
            case 2005: {
                return "CLOB";
            }
            case 70: {
                return "Data Link";
            }
            case 91: {
                return "Date";
            }
            case 3: {
                return "Decimal";
            }
            case 2001: {
                return "Distinct";
            }
            case 8: {
                return "Double";
            }
            case 6: {
                return "Float";
            }
            case 4: {
                return "Integer";
            }
            case 2000: {
                return "Java Object";
            }
            case -4: {
                return "Long Varbinary";
            }
            case -1: {
                return "Long Varchar";
            }
            case 0: {
                return "Null";
            }
            case 2: {
                return "Numeric";
            }
            case 1111: {
                return "Other";
            }
            case 7: {
                return "Real";
            }
            case 2006: {
                return "Ref";
            }
            case 5: {
                return "Small Int";
            }
            case 2002: {
                return "Struct";
            }
            case 92: {
                return "Time";
            }
            case 93: {
                return "Timestamp";
            }
            case -6: {
                return "Tiny Int";
            }
            case -3: {
                return "Varbinary";
            }
            case 12: {
                return "Varchar";
            }
            default: {
                return "Unknown";
            }
        }
    }
    
    public String resolveString(final int index) throws SQLException {
        final int type = this.getSQLColumnType(index);
        switch (type) {
            case -5: {
                final long val = this._rslt.getLong(index);
                return this._rslt.wasNull() ? null : Long.toString(val);
            }
            case -1:
            case 1:
            case 12: {
                final String string = this._rslt.getString(index);
                return this._rslt.wasNull() ? null : string;
            }
            case -6:
            case 5: {
                final short val2 = this._rslt.getShort(index);
                return this._rslt.wasNull() ? null : Short.toString(val2);
            }
            case 4: {
                final int val3 = this._rslt.getInt(index);
                return this._rslt.wasNull() ? null : Integer.toString(val3);
            }
            case 7: {
                final float val4 = this._rslt.getFloat(index);
                return this._rslt.wasNull() ? null : this.convertFloatToString(val4);
            }
            case 6:
            case 8: {
                final double val5 = this._rslt.getDouble(index);
                return this._rslt.wasNull() ? null : this.convertDoubleToString(val5);
            }
            case 2:
            case 3: {
                final BigDecimal val6 = this._rslt.getBigDecimal(index);
                return this._rslt.wasNull() ? null : val6.toPlainString();
            }
            case 91: {
                final Date val7 = this._rslt.getDate(index);
                return this._rslt.wasNull() ? null : val7.toString();
            }
            case 92: {
                final Time val8 = this._rslt.getTime(index);
                return this._rslt.wasNull() ? null : val8.toString();
            }
            case 93: {
                final Timestamp val9 = this._rslt.getTimestamp(index);
                return this._rslt.wasNull() ? null : val9.toString();
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean canResolve(final int index) {
        final int type = this.getSQLColumnType(index);
        switch (type) {
            case -6:
            case -5:
            case -1:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 12:
            case 91:
            case 92:
            case 93: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public int getColumnCount() {
        return this._columnTypes.length;
    }
    
    private String convertDoubleToString(final double d) {
        final String s = Double.toString(d);
        if (!this._useStandardNotationForNumbers) {
            return s;
        }
        if (d == Double.NaN || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
            return s;
        }
        try {
            final BigDecimal bd = new BigDecimal(s);
            return bd.toPlainString();
        }
        catch (NumberFormatException nfe) {
            return s;
        }
    }
    
    private String convertFloatToString(final float f) {
        final String s = Float.toString(f);
        if (!this._useStandardNotationForNumbers) {
            return s;
        }
        if (f == Float.NaN || f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            return s;
        }
        try {
            final BigDecimal bd = new BigDecimal(s);
            return bd.toPlainString();
        }
        catch (NumberFormatException nfe) {
            return s;
        }
    }
}
