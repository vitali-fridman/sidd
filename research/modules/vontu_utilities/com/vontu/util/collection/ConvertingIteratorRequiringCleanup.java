// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public class ConvertingIteratorRequiringCleanup<S, T> implements IteratorRequiringCleanup<T>
{
    private final IteratorRequiringCleanup<S> _source;
    private final Converter<S, T> _itemConverter;
    
    public ConvertingIteratorRequiringCleanup(final IteratorRequiringCleanup<S> source, final Converter<S, T> itemConverter) {
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
    public void cleanUp() {
        this._source.cleanUp();
    }
}
