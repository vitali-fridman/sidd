// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.nlp.lexer.TokenPosition;
import com.vontu.detection.output.DatabaseTokenMarker;

class DatabaseRowMarker implements DatabaseTokenMarker
{
    private final int _databaseRow;
    private final int _line;
    private final int _offset;
    private final int _span;
    
    DatabaseRowMarker(final int databaseRow, final TokenPosition tokenPosition) {
        this._databaseRow = databaseRow;
        this._offset = tokenPosition.start;
        this._span = tokenPosition.length;
        this._line = tokenPosition.line;
    }
    
    public int databaseRow() {
        return this._databaseRow;
    }
    
    public int offset() {
        return this._offset;
    }
    
    public int span() {
        return this._span;
    }
    
    public int line() {
        return this._line;
    }
    
    public String hint() {
        return null;
    }
}
