// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.CharArraySlice;

class CharSequenceCharArrayConverter implements CharArrayProvider
{
    private final CharSequence source;
    
    CharSequenceCharArrayConverter(final CharSequence source) {
        this.source = source;
    }
    
    @Override
    public CharArraySlice getCharArray() {
        final char[] chars = new char[this.source.length()];
        for (int i = 0; i < this.source.length(); ++i) {
            chars[i] = this.source.charAt(i);
        }
        return new CharArraySlice(chars, 0, chars.length);
    }
}
