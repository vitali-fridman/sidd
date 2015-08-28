// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.nlp.lexer.TextToken;
import java.util.List;
import com.vontu.nlp.lexer.TokenSet;
import com.vontu.nlp.lexer.TabularTokenSet;

final class TokenRange
{
    private final int _start;
    private final int _end;
    
    TokenRange(final TabularTokenSet tabularTokenSet) {
        this(getTabularDataStart((TokenSet)tabularTokenSet), getTabularDataEnd((TokenSet)tabularTokenSet));
    }
    
    TokenRange(final int start, final int end) {
        this._start = start;
        this._end = end;
    }
    
    private static int getTabularDataStart(final TokenSet tokenSet) {
        final List firstRow = (List)tokenSet.get(0);
        final TextToken firstToken = firstRow.get(0);
        return firstToken.getIndex();
    }
    
    private static int getTabularDataEnd(final TokenSet tokenSet) {
        final int numberOfRows = tokenSet.size();
        final List lastRow = (List)tokenSet.get(numberOfRows - 1);
        final TextToken lastToken = lastRow.get(lastRow.size() - 1);
        return lastToken.getIndex();
    }
    
    int start() {
        return this._start;
    }
    
    int end() {
        return this._end;
    }
    
    int length() {
        return this._end - this._start + 1;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this._start;
        result = 37 * result + this._end;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TokenRange)) {
            return false;
        }
        final TokenRange other = (TokenRange)obj;
        return this._end == other._end && this._start == other._start;
    }
}
