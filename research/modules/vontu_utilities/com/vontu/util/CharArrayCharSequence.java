// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.Serializable;

public class CharArrayCharSequence extends CharArraySlice implements CharSequence, Serializable
{
    private static final long serialVersionUID = 82384728L;
    private final char[] _charArray;
    private final int _offset;
    private final int _length;
    
    public CharArrayCharSequence(final char[] charArray) {
        this(charArray, 0, charArray.length);
    }
    
    public CharArrayCharSequence(final char[] charArray, final int start, final int end) {
        super(charArray, start, end);
        this._charArray = charArray;
        this._offset = start;
        this._length = end - start;
    }
    
    @Override
    public int length() {
        return this._length;
    }
    
    @Override
    public char charAt(final int index) {
        return this._charArray[this._offset + index];
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        if (start < 0 || end > this._length || start > end) {
            throw new IllegalArgumentException("Invalid range");
        }
        return new CharArrayCharSequence(this._charArray, this._offset + start, this._offset + end);
    }
    
    public int copy(final int seqOffset, final char[] buffer, final int bufferOffset, int length) {
        if (seqOffset + length > this.length()) {
            length = this.length() - seqOffset;
        }
        if (length < 0 || length + bufferOffset > buffer.length) {
            throw new IndexOutOfBoundsException("Invalid length: " + length + ", buffer size: " + buffer.length);
        }
        System.arraycopy(this._charArray, seqOffset, buffer, bufferOffset, length);
        return length;
    }
    
    @Override
    public String toString() {
        return new String(this._charArray, this._offset, this._length);
    }
}
