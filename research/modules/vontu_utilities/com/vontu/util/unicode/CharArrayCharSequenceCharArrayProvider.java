// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.CharArraySlice;
import com.vontu.util.CharArrayCharSequence;

class CharArrayCharSequenceCharArrayProvider implements CharArrayProvider
{
    private final CharArrayCharSequence _source;
    
    CharArrayCharSequenceCharArrayProvider(final CharArrayCharSequence source) {
        this._source = source;
    }
    
    @Override
    public CharArraySlice getCharArray() {
        return this._source;
    }
}
