// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;

public class IterableConverter<S, T> implements Iterable<T>
{
    private final Iterable<S> _source;
    private final Converter<S, T> _itemConverter;
    
    public IterableConverter(final Iterable<S> source, final Converter<S, T> itemConverter) {
        this._source = source;
        this._itemConverter = itemConverter;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ConvertingIterator<S, T>(this._source.iterator(), this._itemConverter);
    }
}
