// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;

public class EmptyIteratorRequiringCleanup<E> implements IteratorRequiringCleanup<E>
{
    @Override
    public boolean hasNext() {
        return false;
    }
    
    @Override
    public E next() throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    public void remove() throws IllegalStateException {
        throw new IllegalStateException();
    }
    
    @Override
    public void cleanUp() {
    }
}
