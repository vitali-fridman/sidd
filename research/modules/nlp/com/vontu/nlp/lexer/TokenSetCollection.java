// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class TokenSetCollection
{
    private List _collection;
    private int _type;
    private static int INITIAL_CAPACITY;
    
    public TokenSetCollection(final int type) {
        this._type = type;
        this._collection = new ArrayList(TokenSetCollection.INITIAL_CAPACITY);
    }
    
    public int geType() {
        return this._type;
    }
    
    public List getTokenSets() {
        return Collections.unmodifiableList((List<?>)this._collection);
    }
    
    public void add(final TokenSet tokenSet) {
        this._collection.add(tokenSet);
    }
    
    public int size() {
        return this._collection.size();
    }
    
    static {
        TokenSetCollection.INITIAL_CAPACITY = 128;
    }
}
