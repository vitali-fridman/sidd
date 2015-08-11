// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.nlp.lexer.TokenType;
import java.util.Iterator;
import java.io.PrintWriter;
import cern.colt.list.ObjectArrayList;
import com.vontu.nlp.lexer.pattern.SystemPattern;
import com.vontu.nlp.lexer.Token;
import java.util.LinkedList;
import cern.colt.map.OpenIntObjectHashMap;
import java.util.Collection;

class StatisticsBuilder implements DataSourceStatistics
{
    private int _cellCount;
    private int _emptyCellCount;
    private int _invalidRowCount;
    private final int _profileColumnCount;
    private int _rowCount;
    private int _rowCellCount;
    private int _tooLongRowCount;
    private int _tooShortRowCount;
    private Collection _rowErrors;
    private final OpenIntObjectHashMap _columnStatsMap;
    
    StatisticsBuilder(final DatabaseProfileDescriptor descriptor) {
        this._rowErrors = new LinkedList();
        this._profileColumnCount = descriptor.columns().length;
        fillColumnStatsMap(this._columnStatsMap = new OpenIntObjectHashMap(this._profileColumnCount), descriptor.columns());
    }
    
    private void appendColumnStats(final StringBuffer messageBuilder) {
        final DataSourceColumnStatistics[] columnStats = this.columnStatistics();
        for (int i = 0; i < columnStats.length; ++i) {
            if (i > 0) {
                messageBuilder.append('\n');
            }
            messageBuilder.append(String.valueOf(columnStats[i]));
        }
    }
    
    private static void fillColumnStatsMap(final OpenIntObjectHashMap map, final DatabaseProfileColumn[] columns) {
        for (int i = 0; i < columns.length; ++i) {
            map.put(columns[i].index(), (Object)new ColumnStatistics(columns[i].index()));
        }
    }
    
    boolean isCurrentRowInvalid() {
        return this._rowErrors.size() > 0;
    }
    
    private void addRowError(final Object error) {
        if (!this.isCurrentRowInvalid()) {
            ++this._invalidRowCount;
        }
        this._rowErrors.add(error);
    }
    
    void incrementEmptyCellCount(final int columnIndex) {
        ++this._emptyCellCount;
        final ColumnStatistics cs = (ColumnStatistics)this._columnStatsMap.get(columnIndex);
        if (cs != null) {
            cs.incrementEmptyCellCount();
        }
    }
    
    void validateColumnCount(final int actualCount) {
        if (actualCount > this._profileColumnCount) {
            ++this._tooLongRowCount;
            this.addRowError(new RowTooLongError(this._profileColumnCount, actualCount));
        }
        else if (actualCount < this._profileColumnCount) {
            ++this._tooShortRowCount;
        }
    }
    
    void addFormatError(final Token token, final int columnIndex, final SystemPattern systemPattern) {
        this.addRowError(new InvalidFormatError(systemPattern, token));
        ((ColumnStatistics)this._columnStatsMap.get(columnIndex)).incrementInvalidCellCount();
    }
    
    void addRow() {
        ++this._rowCount;
        this._rowCellCount = 0;
        this._rowErrors.clear();
    }
    
    @Override
    public int rowCount() {
        return this._rowCount;
    }
    
    @Override
    public int invalidRowCount() {
        return this._invalidRowCount;
    }
    
    void addRowCellCountToTotal() {
        this._cellCount += this._rowCellCount;
    }
    
    int incrementRowCellCount() {
        return ++this._rowCellCount;
    }
    
    @Override
    public int cellCount() {
        return this._cellCount;
    }
    
    @Override
    public int tooShortRowCount() {
        return this._tooShortRowCount;
    }
    
    @Override
    public int tooLongRowCount() {
        return this._tooLongRowCount;
    }
    
    @Override
    public int noDataCellCount() {
        return this._emptyCellCount;
    }
    
    @Override
    public DataSourceColumnStatistics[] columnStatistics() {
        final ObjectArrayList columnsStats = this._columnStatsMap.values();
        columnsStats.quickSortFromTo(0, columnsStats.size() - 1);
        return (DataSourceColumnStatistics[])columnsStats.toArray((Object[])new DataSourceColumnStatistics[columnsStats.size()]);
    }
    
    @Override
    public String toString() {
        final StringBuffer messageBuilder = new StringBuffer();
        messageBuilder.append("Rows processed: ").append(this._rowCount).append('\n');
        messageBuilder.append("Invalid rows: ").append(this._invalidRowCount).append('\n');
        messageBuilder.append("Cells with no data: ").append(this._emptyCellCount).append('\n');
        messageBuilder.append("Rows with too many columns: ").append(this._tooLongRowCount).append('\n');
        messageBuilder.append("Rows with too few columns: ").append(this._tooShortRowCount).append('\n');
        messageBuilder.append("Results per column:\n");
        this.appendColumnStats(messageBuilder);
        messageBuilder.append("\nCells indexed total: ").append(this._cellCount);
        return messageBuilder.toString();
    }
    
    void writeRowErrors(final PrintWriter writer) {
        final Iterator iterator = this._rowErrors.iterator();
        while (iterator.hasNext()) {
            writer.print(iterator.next());
            writer.print(' ');
        }
    }
    
    private static final class RowTooLongError
    {
        private final int _actual;
        private final int _expected;
        
        RowTooLongError(final int expected, final int actual) {
            this._expected = expected;
            this._actual = actual;
        }
        
        @Override
        public String toString() {
            return "Too many fields (" + this._actual + " instead of " + this._expected + ").";
        }
    }
    
    private static final class InvalidFormatError
    {
        private final SystemPattern _systemPattern;
        private final Token _token;
        
        InvalidFormatError(final SystemPattern systemPattern, final Token token) {
            this._systemPattern = systemPattern;
            this._token = token;
        }
        
        @Override
        public String toString() {
            return "Token \"" + this._token.getValue() + "\" (type " + TokenType.getName(this._token.getType()) + ") doesn't conform to system pattern " + this._systemPattern + '.';
        }
    }
    
    private static final class ColumnStatistics implements DataSourceColumnStatistics, Comparable
    {
        private final int _columnIndex;
        private int _emptyCellCount;
        private int _invalidCellCount;
        
        ColumnStatistics(final int columnIndex) {
            this._columnIndex = columnIndex;
        }
        
        @Override
        public int column() {
            return this._columnIndex;
        }
        
        @Override
        public int compareTo(final Object o) {
            if (!(o instanceof ColumnStatistics)) {
                throw new ClassCastException();
            }
            if (this._columnIndex > ((ColumnStatistics)o)._columnIndex) {
                return 1;
            }
            if (this._columnIndex < ((ColumnStatistics)o)._columnIndex) {
                return -1;
            }
            return 0;
        }
        
        void incrementEmptyCellCount() {
            ++this._emptyCellCount;
        }
        
        void incrementInvalidCellCount() {
            ++this._invalidCellCount;
        }
        
        @Override
        public int invalidCellCount() {
            return this._invalidCellCount;
        }
        
        @Override
        public int noDataCellCount() {
            return this._emptyCellCount;
        }
        
        @Override
        public String toString() {
            return "Column " + this._columnIndex + ":  Invalid cells: " + this._invalidCellCount + "; Cells with no data: " + this._emptyCellCount;
        }
    }
}
