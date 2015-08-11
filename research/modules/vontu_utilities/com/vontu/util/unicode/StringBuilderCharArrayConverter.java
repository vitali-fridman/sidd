// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.vontu.util.CharArraySlice;

class StringBuilderCharArrayConverter implements CharArrayProvider
{
    private final StringBuilder _source;
    
    StringBuilderCharArrayConverter(final StringBuilder source) {
        this._source = source;
    }
    
    @Override
    public CharArraySlice getCharArray() {
        final char[] chars = new char[this._source.length()];
        this._source.getChars(0, this._source.length(), chars, 0);
        return new CharArraySlice(chars, 0, chars.length);
    }
}
