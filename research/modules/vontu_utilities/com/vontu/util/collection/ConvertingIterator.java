// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;

public class ConvertingIterator<S, T> implements Iterator<T>
{
    private final Iterator<S> _source;
    private final Converter<S, T> _itemConverter;
    
    public ConvertingIterator(final Iterator<S> source, final Converter<S, T> itemConverter) {
        this._source = source;
        this._itemConverter = itemConverter;
    }
    
    @Override
    public boolean hasNext() {
        return this._source.hasNext();
    }
    
    @Override
    public T next() {
        return this._itemConverter.convert(this._source.next());
    }
    
    @Override
    public void remove() {
        this._source.remove();
    }
}
