// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

public class UpcastConverter<Super, Sub extends Super> implements Converter<Sub, Super>
{
    @Override
    public Super convert(final Sub source) {
        return source;
    }
}
