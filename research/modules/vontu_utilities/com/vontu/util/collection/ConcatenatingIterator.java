// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;

public class ConcatenatingIterator<T> implements Iterator<T>
{
    private final Iterator<Iterator<T>> _baseIterators;
    private Iterator<T> _curIterator;
    
    public ConcatenatingIterator(final Iterator<T>... baseIterators) {
        this._baseIterators = Arrays.asList(baseIterators).iterator();
    }
    
    public ConcatenatingIterator(final Iterator<Iterator<T>> baseIterators) {
        this._baseIterators = baseIterators;
    }
    
    public static <T, S extends Collection<T>> ConcatenatingIterator<T> makeConcatenatingIterator(final Iterator<S> baseCollectionIterator) {
        final Converter<S, Iterator<T>> converter = new Converter<S, Iterator<T>>() {
            @Override
            public Iterator<T> convert(final S source) {
                return source.iterator();
            }
        };
        return new ConcatenatingIterator<T>(new ConvertingIterator<S, Iterator<T>>(baseCollectionIterator, converter));
    }
    
    @Override
    public boolean hasNext() {
        while (this._curIterator == null || !this._curIterator.hasNext()) {
            if (!this._baseIterators.hasNext()) {
                return false;
            }
            this._curIterator = this._baseIterators.next();
        }
        return this._curIterator != null && this._curIterator.hasNext();
    }
    
    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this._curIterator.next();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
