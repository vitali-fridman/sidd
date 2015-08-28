// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

public class TokenPosition
{
    public static final int LINE_UNKNOWN = -1;
    public static final int COLUMN_UNKNOWN = -1;
    public int start;
    public int length;
    public int line;
    public int column;
    
    public TokenPosition(final int start, final int length, final int line, final int column) {
        this.start = start;
        this.length = length;
        this.line = line;
        this.column = column;
    }
}
