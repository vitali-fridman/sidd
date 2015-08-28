// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import java.util.HashMap;
import java.util.Iterator;
import com.vontu.model.data.DataSourceColumn;
import java.util.LinkedList;
import com.vontu.profileindexer.database.DatabaseProfileColumn;
import java.util.Collection;
import com.vontu.model.data.DataSource;
import com.vontu.nlp.lexer.pattern.SystemPattern;
import java.util.Map;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;

public final class DataSourceAdapter implements DatabaseProfileDescriptor
{
    public static final char DEFAULT_SEPARATOR = '\t';
    private static final Map<Integer, SystemPattern> _systemPatternMap;
    private final DataSource _dataSource;
    private final char _separator;
    private final Collection<DatabaseProfileColumn> _columns;
    
    public DataSourceAdapter(final DataSource dataSource) {
        this._columns = new LinkedList<DatabaseProfileColumn>();
        this._dataSource = dataSource;
        this._separator = getSeparatorChar(dataSource.getColumnSeparator());
        final Iterator<DataSourceColumn> it = (Iterator<DataSourceColumn>)dataSource.dataSourceColumnIterator();
        while (it.hasNext()) {
            this._columns.add((DatabaseProfileColumn)new DataSourceColumnAdapter((DataSourceColumn)it.next()));
        }
    }
    
    private static char getSeparatorChar(final String separator) {
        return (separator == null) ? '\t' : separator.toCharArray()[0];
    }
    
    public String name() {
        return this._dataSource.getName();
    }
    
    public DatabaseProfileColumn[] columns() {
        return this._columns.toArray(new DatabaseProfileColumn[this._columns.size()]);
    }
    
    public boolean hasHeaders() {
        return this._dataSource.getIsFirstRowColumnHeadings() == 1;
    }
    
    public int errorThreshold() {
        return this._dataSource.getErrorThreshold();
    }
    
    public char columnSeparator() {
        return this._separator;
    }
    
    private static SystemPattern getSystemPatternForType(final int patternType) {
        return DataSourceAdapter._systemPatternMap.get(new Integer(patternType));
    }
    
    static {
        (_systemPatternMap = new HashMap<Integer, SystemPattern>()).put(new Integer(4), SystemPattern.CCN);
        DataSourceAdapter._systemPatternMap.put(new Integer(2), SystemPattern.EMAIL);
        DataSourceAdapter._systemPatternMap.put(new Integer(8), SystemPattern.IP_ADDRESS);
        DataSourceAdapter._systemPatternMap.put(new Integer(6), SystemPattern.NUMBER);
        DataSourceAdapter._systemPatternMap.put(new Integer(7), SystemPattern.PERCENT);
        DataSourceAdapter._systemPatternMap.put(new Integer(1), SystemPattern.PHONE);
        DataSourceAdapter._systemPatternMap.put(new Integer(9), SystemPattern.POSTAL_CODE);
        DataSourceAdapter._systemPatternMap.put(new Integer(3), SystemPattern.TAXID);
    }
    
    private static final class DataSourceColumnAdapter implements DatabaseProfileColumn
    {
        private final DataSourceColumn _column;
        private final SystemPattern _systemPattern;
        
        private DataSourceColumnAdapter(final DataSourceColumn column) {
            this._column = column;
            this._systemPattern = ((this._column.getPatternType() < 0) ? null : getSystemPatternForType(this._column.getPatternType()));
        }
        
        public int index() {
            return this._column.getColumnNumber();
        }
        
        public SystemPattern systemPattern() {
            return this._systemPattern;
        }
    }
}
