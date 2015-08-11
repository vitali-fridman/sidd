// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.CharArraySlice;
import java.nio.CharBuffer;

class CharBufferCharArrayConverter implements CharArrayProvider
{
    private CharBuffer charBuffer;
    private CharArrayProvider defaultConverter;
    
    CharBufferCharArrayConverter(final CharBuffer charBuffer) {
        this(charBuffer, new CharSequenceCharArrayConverter(charBuffer));
    }
    
    CharBufferCharArrayConverter(final CharBuffer charBuffer, final CharArrayProvider defaultConverter) {
        this.charBuffer = charBuffer;
        this.defaultConverter = defaultConverter;
    }
    
    @Override
    public CharArraySlice getCharArray() {
        if (this.charBuffer.hasArray()) {
            return new CharArraySlice(this.charBuffer.array(), this.charBuffer.arrayOffset() + this.charBuffer.position(), this.charBuffer.arrayOffset() + this.charBuffer.limit() - 1);
        }
        return this.defaultConverter.getCharArray();
    }
}
