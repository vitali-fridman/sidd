// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode.converter;

public class ByteCharIndex
{
    public int byteIndex;
    public int charIndex;
    
    public ByteCharIndex() {
        this(0);
    }
    
    public ByteCharIndex(final int initialByteOffset) {
        this.byteIndex = 0;
        this.charIndex = 0;
        this.byteIndex = initialByteOffset;
    }
    
    public int incrCharIndex() {
        return this.charIndex++;
    }
    
    public int incrByteIndex() {
        return this.byteIndex++;
    }
    
    public void addByteIndex(final int add) {
        this.byteIndex += add;
    }
    
    public void addCharIndex(final int add) {
        this.byteIndex += add;
    }
}
