// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.Iterator;

public class FilterIterable<T> implements Iterable<T>
{
    private final Iterable<T> _source;
    private final Filter<T> _filter;
    
    public FilterIterable(final Iterable<T> source, final Filter<T> filter) {
        this._source = source;
        this._filter = filter;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new FilteringIterator<T>(this._source.iterator(), this._filter);
    }
}
