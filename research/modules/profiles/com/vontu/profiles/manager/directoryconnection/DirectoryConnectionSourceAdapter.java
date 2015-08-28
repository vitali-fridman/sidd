// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import com.vontu.nlp.lexer.pattern.SystemPattern;
import com.vontu.profileindexer.database.DatabaseProfileColumn;
import com.vontu.model.data.DirectoryConnectionSource;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;

public class DirectoryConnectionSourceAdapter implements DatabaseProfileDescriptor
{
    private final DirectoryConnectionSource directoryConnectionSource;
    private final DatabaseProfileColumn[] databaseProfileColumns;
    
    public DirectoryConnectionSourceAdapter(final DirectoryConnectionSource directoryConnectionSource) {
        this.directoryConnectionSource = directoryConnectionSource;
        (this.databaseProfileColumns = new DatabaseProfileColumn[2])[0] = (DatabaseProfileColumn)new DirectoryConnectionSourceColumn(1, SystemPattern.EMAIL);
        this.databaseProfileColumns[1] = (DatabaseProfileColumn)new DirectoryConnectionSourceColumn(2, SystemPattern.NUMBER);
    }
    
    public char columnSeparator() {
        return '|';
    }
    
    public DatabaseProfileColumn[] columns() {
        return this.databaseProfileColumns;
    }
    
    public int errorThreshold() {
        return 0;
    }
    
    public boolean hasHeaders() {
        return false;
    }
    
    public String name() {
        return this.directoryConnectionSource.getName();
    }
    
    private static final class DirectoryConnectionSourceColumn implements DatabaseProfileColumn
    {
        private final int columnIndex;
        private final SystemPattern systemPattern;
        
        private DirectoryConnectionSourceColumn(final int columnIndex, final SystemPattern systemPattern) {
            this.columnIndex = columnIndex;
            this.systemPattern = systemPattern;
        }
        
        public int index() {
            return this.columnIndex;
        }
        
        public SystemPattern systemPattern() {
            return this.systemPattern;
        }
    }
}
