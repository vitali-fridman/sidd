// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public class IdentityConverter<T> implements Converter<T, T>
{
    @Override
    public T convert(final T source) {
        return source;
    }
}
