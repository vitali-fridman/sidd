// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;

public class IteratorRequiringCleanupWalker<T> implements Walker<T>
{
    private final IteratorRequiringCleanup<T> _baseIterator;
    private T _currentEntry;
    private boolean _isCleanedUp;
    
    public IteratorRequiringCleanupWalker(final IteratorRequiringCleanup<T> baseIterator) {
        this._isCleanedUp = false;
        this._baseIterator = baseIterator;
        if (this._baseIterator.hasNext()) {
            this._currentEntry = this._baseIterator.next();
        }
    }
    
    public IteratorRequiringCleanupWalker(final Iterator<T> baseIterator) {
        this((IteratorRequiringCleanup)new IteratorRequiringCleanupAdapter(baseIterator));
    }
    
    @Override
    public void cleanUp() {
        this._isCleanedUp = true;
        this._baseIterator.cleanUp();
    }
    
    @Override
    public boolean isCleanedUp() {
        return this._isCleanedUp;
    }
    
    @Override
    public T getCurrentEntry() {
        return this._currentEntry;
    }
    
    @Override
    public T advance() {
        if (this._baseIterator.hasNext()) {
            this._currentEntry = this._baseIterator.next();
        }
        else {
            this._currentEntry = null;
        }
        return this._currentEntry;
    }
}
