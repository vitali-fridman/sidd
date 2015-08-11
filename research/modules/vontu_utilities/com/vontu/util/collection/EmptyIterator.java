// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class EmptyIterator<E> implements Iterator<E>
{
    @Override
    public boolean hasNext() {
        return false;
    }
    
    @Override
    public E next() throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() throws IllegalStateException {
        throw new IllegalStateException();
    }
}
