// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.NoSuchElementException;
import java.util.Iterator;

abstract class TokenIndexIterator implements Iterator
{
    private final int[] _tokenIds;
    private int _index;
    
    TokenIndexIterator(final int[] tokenIds) {
        this._tokenIds = tokenIds;
        this._index = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this._index < this._tokenIds.length;
    }
    
    protected abstract Object getNext(final int p0);
    
    @Override
    public Object next() {
        if (this.hasNext()) {
            return this.getNext(this._tokenIds[this._index++]);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
