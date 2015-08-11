// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.discover;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.CompareToBuilder;
import com.vontu.util.StringUtil;
import java.util.logging.Logger;
import java.io.Serializable;

public class SQLURI implements Comparable<SQLURI>, Serializable
{
    public static final Logger _logger;
    private final String _uriText;
    private final String _contentRoot;
    private final String _tableName;
    private final Integer _rowNumber;
    private final String _alias;
    private final String _dbType;
    
    public SQLURI(final String uriText) {
        this._uriText = uriText;
        if (uriText != null && !StringUtil.isEmpty(uriText)) {
            final String[] parts = uriText.split("\\|");
            if (parts.length > 3) {
                throw new IllegalArgumentException("SQL URI is in an invalid format: " + uriText);
            }
            this._contentRoot = extractContentRoot(parts);
            this._dbType = extractDbType(uriText);
            this._alias = extractAlias(uriText);
            this._tableName = extractTableName(parts);
            this._rowNumber = extractRowNumber(parts);
        }
        else {
            this._contentRoot = null;
            this._dbType = null;
            this._alias = null;
            this._tableName = null;
            this._rowNumber = null;
        }
    }
    
    public boolean isEmpty() {
        return StringUtil.isEmpty(this._contentRoot);
    }
    
    public String getURIText() {
        return this._uriText;
    }
    
    @Override
    public String toString() {
        return this._uriText;
    }
    
    public String getContentRoot() {
        return this._contentRoot;
    }
    
    public String getAlias() {
        return this._alias;
    }
    
    public String getDbType() {
        return this._dbType;
    }
    
    public String getTableName() {
        return this._tableName;
    }
    
    public Integer getRowNumber() {
        return this._rowNumber;
    }
    
    @Override
    public int compareTo(final SQLURI other) {
        if (other == null) {
            throw new NullPointerException();
        }
        return new CompareToBuilder().append((Object)this._contentRoot, (Object)other._contentRoot).append((Object)this._tableName, (Object)other._tableName).append((Object)this._rowNumber, (Object)other._rowNumber).toComparison();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SQLURI)) {
            return false;
        }
        final SQLURI otherSQLURI = (SQLURI)o;
        return new EqualsBuilder().append((Object)this._contentRoot, (Object)otherSQLURI._contentRoot).append((Object)this._tableName, (Object)otherSQLURI._tableName).append((Object)this._rowNumber, (Object)otherSQLURI._rowNumber).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 47).append((Object)this._contentRoot).append((Object)this._tableName).append((Object)this._rowNumber).toHashCode();
    }
    
    private static String extractContentRoot(final String[] uriParts) {
        return uriParts[0];
    }
    
    private static String extractDbType(final String uriText) {
        final int index = uriText.indexOf(":");
        if (index < 0) {
            throw new IllegalArgumentException("Invalid SQL Target URI, unable to determine database type");
        }
        return uriText.substring(0, index);
    }
    
    private static String extractAlias(final String uriText) {
        final int index = uriText.indexOf(":");
        if (index < 0) {
            throw new IllegalArgumentException("Invalid SQL Target URI, unable to determine database alias");
        }
        return uriText.substring(index + 1);
    }
    
    private static Integer extractRowNumber(final String[] uriParts) {
        if (uriParts == null || uriParts.length < 3) {
            return null;
        }
        final String rowNumberString = uriParts[2];
        final Integer rowNumber = Integer.parseInt(rowNumberString);
        if (rowNumber < 0) {
            throw new IllegalArgumentException("Row number can not be less than zero");
        }
        if (rowNumber == 0) {
            return null;
        }
        return rowNumber;
    }
    
    private static String extractTableName(final String[] uriParts) {
        if (uriParts == null || uriParts.length < 2) {
            return null;
        }
        return uriParts[1];
    }
    
    static {
        _logger = Logger.getLogger(SQLURI.class.getName());
    }
}
