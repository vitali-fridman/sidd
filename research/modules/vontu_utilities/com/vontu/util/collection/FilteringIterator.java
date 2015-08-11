// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class FilteringIterator<T> implements Iterator<T>
{
    private final Iterator<T> _sourceIterator;
    private final Filter<? super T> _filter;
    private boolean _hasNext;
    private boolean _initialized;
    private T _next;
    
    public FilteringIterator(final Iterator<T> sourceIterator, final Filter<? super T> filter) {
        this._sourceIterator = sourceIterator;
        this._filter = filter;
    }
    
    @Override
    public boolean hasNext() {
        if (!this._initialized) {
            this.positionToNext();
            this._initialized = true;
        }
        return this._hasNext;
    }
    
    private T moveNext() {
        return this._next = this._sourceIterator.next();
    }
    
    @Override
    public T next() {
        if (!this._initialized) {
            this.positionToNext();
            this._initialized = true;
        }
        if (!this._hasNext) {
            throw new NoSuchElementException();
        }
        final T next = this._next;
        this.positionToNext();
        return next;
    }
    
    private void positionToNext() {
        do {
            this._hasNext = this._sourceIterator.hasNext();
        } while (this._hasNext && !this._filter.shouldAccept(this.moveNext()));
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
