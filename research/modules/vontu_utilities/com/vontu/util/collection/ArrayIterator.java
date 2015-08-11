// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayIterator implements Iterator
{
    private Object _array;
    private int _arraySize;
    private int _index;
    
    public ArrayIterator(final Object array) {
        this._index = 0;
        this._array = array;
        this._arraySize = Array.getLength(this._array);
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object next() {
        if (this._index < this._arraySize) {
            return Array.get(this._array, this._index++);
        }
        throw new NoSuchElementException(Integer.toString(this._index));
    }
    
    @Override
    public boolean hasNext() {
        return this._index < this._arraySize;
    }
}
