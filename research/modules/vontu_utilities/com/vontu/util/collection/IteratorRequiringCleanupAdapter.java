// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;

public class IteratorRequiringCleanupAdapter<T> implements IteratorRequiringCleanup<T>
{
    private final Iterator<T> _iterator;
    
    public IteratorRequiringCleanupAdapter(final Iterator<T> iterator) {
        this._iterator = iterator;
    }
    
    @Override
    public boolean hasNext() {
        return this._iterator.hasNext();
    }
    
    @Override
    public T next() {
        return this._iterator.next();
    }
    
    @Override
    public void cleanUp() {
    }
}
