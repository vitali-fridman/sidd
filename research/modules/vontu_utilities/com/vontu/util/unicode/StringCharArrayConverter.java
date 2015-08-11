// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.CharArraySlice;

class StringCharArrayConverter implements CharArrayProvider
{
    private final String _source;
    
    StringCharArrayConverter(final String source) {
        this._source = source;
    }
    
    @Override
    public CharArraySlice getCharArray() {
        return new CharArraySlice(this._source.toCharArray(), 0, this._source.length());
    }
}
