// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class DeduplicatingIterator<T> implements Iterator<T>
{
    private Iterator<T> _baseIterator;
    private Set<T> _elementsSoFar;
    private T _next;
    
    public DeduplicatingIterator(final Iterator<T> it) {
        this._elementsSoFar = new HashSet<T>();
        this._baseIterator = it;
    }
    
    @Override
    public boolean hasNext() {
        while (this._next == null && this._baseIterator.hasNext()) {
            final T candidate = this._baseIterator.next();
            if (!this._elementsSoFar.contains(candidate)) {
                this._next = candidate;
                this._elementsSoFar.add(candidate);
            }
        }
        if (this._next == null) {
            this._elementsSoFar = null;
            return false;
        }
        return true;
    }
    
    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final T result = this._next;
        this._next = null;
        return result;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
