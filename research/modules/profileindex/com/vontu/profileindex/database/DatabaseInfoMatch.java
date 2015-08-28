// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.Iterator;
import java.util.Collections;
import com.vontu.nlp.lexer.Token;
import java.util.LinkedList;
import java.util.Collection;

final class DatabaseInfoMatch
{
    private final int _rowNumber;
    private final Collection _tokens;
    
    DatabaseInfoMatch(final int rowNumber) {
        this._tokens = new LinkedList();
        this._rowNumber = rowNumber;
    }
    
    void addToken(final Token token) {
        this._tokens.add(token);
    }
    
    public Collection getMatchedTokens() {
        return Collections.unmodifiableCollection((Collection<?>)this._tokens);
    }
    
    public int getRowNumber() {
        return this._rowNumber;
    }
    
    @Override
    public String toString() {
        String retString = "Matched SDP for row number " + this._rowNumber + "::";
        retString = retString + "Number of tokens in this match: " + this._tokens.size() + "\n";
        for (final Token token : this._tokens) {
            retString += token.toString();
        }
        return retString;
    }
}
