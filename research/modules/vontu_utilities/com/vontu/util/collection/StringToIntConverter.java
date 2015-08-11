// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public class StringToIntConverter implements Converter<String, Integer>
{
    @Override
    public Integer convert(final String source) {
        return Integer.parseInt(source);
    }
}
