// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.bytestring;

import java.io.UnsupportedEncodingException;
import com.hunnysoft.jmime.ByteString;

public final class ReusableByteStringBuffer
{
    private static final int DEFAULT_LENGTH = 32;
    private static final String STRING_ENCODING = "ISO-8859-1";
    private byte[] _bytes;
    private int _length;
    private final int _maxLength;
    
    public ReusableByteStringBuffer() {
        this(32);
    }
    
    public ReusableByteStringBuffer(final int initialLength) {
        this(initialLength, Integer.MAX_VALUE);
    }
    
    public ReusableByteStringBuffer(final int initialLength, final int maxLength) {
        if (initialLength > maxLength) {
            throw new IllegalArgumentException("initialLength > maxLength");
        }
        this._bytes = new byte[initialLength];
        this._length = 0;
        this._maxLength = maxLength;
    }
    
    private void checkMaxLength(final int sizeToAppend) throws IllegalStateException {
        if (this._length + sizeToAppend > this._maxLength) {
            throw new IllegalArgumentException("Unable to grow the buffer beyond the maximum length of " + this._maxLength + " bytes.");
        }
    }
    
    public ReusableByteStringBuffer append(final ByteString bstr) {
        final byte[] bytes = bstr.data();
        final int offset = bstr.offset();
        final int length = bstr.length();
        this.append(bytes, offset, length);
        return this;
    }
    
    public ReusableByteStringBuffer append(final String str) {
        byte[] bytes;
        try {
            bytes = str.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            bytes = new byte[0];
        }
        this.append(bytes);
        return this;
    }
    
    public ReusableByteStringBuffer append(final byte[] bytes) throws IllegalStateException {
        return this.append(bytes, 0, bytes.length);
    }
    
    public ReusableByteStringBuffer append(final byte[] bytes, final int offset, final int length) throws IllegalStateException {
        this.checkMaxLength(length);
        if (this._length + length > this._bytes.length) {
            this.grow(length);
        }
        System.arraycopy(bytes, offset, this._bytes, this._length, length);
        this._length += length;
        return this;
    }
    
    public ReusableByteStringBuffer append(final byte c) throws IllegalStateException {
        this.checkMaxLength(1);
        if (this._length + 1 > this._bytes.length) {
            this.grow(1);
        }
        this._bytes[this._length] = c;
        ++this._length;
        return this;
    }
    
    public ReusableByteStringBuffer append(final char c) {
        final byte by = (byte)c;
        this.append(by);
        return this;
    }
    
    public ReusableByteStringBuffer append(final int i) {
        final String s = String.valueOf(i);
        this.append(s);
        return this;
    }
    
    public ReusableByteStringBuffer append(final long n) {
        final String s = String.valueOf(n);
        this.append(s);
        return this;
    }
    
    public ReusableByteStringBuffer append(final float f) {
        final String s = String.valueOf(f);
        this.append(s);
        return this;
    }
    
    public ReusableByteStringBuffer append(final double x) {
        final String s = String.valueOf(x);
        this.append(s);
        return this;
    }
    
    public int capacity() {
        return this._bytes.length;
    }
    
    public void ensureCapacity(final int n) {
        this.checkMaxLength(n - this._bytes.length);
        if (n > this._bytes.length) {
            final byte[] bytes = new byte[n];
            System.arraycopy(this._bytes, 0, bytes, 0, this._length);
            this._bytes = bytes;
        }
    }
    
    private void grow(final int sizeToAppend) {
        int newLength = 2 * this._bytes.length;
        if (sizeToAppend + this._length > newLength) {
            newLength = sizeToAppend + this._length;
        }
        if (newLength < 32) {
            newLength = 32;
        }
        if (newLength > this._maxLength) {
            newLength = this._maxLength;
        }
        final byte[] newBytes = new byte[newLength];
        System.arraycopy(this._bytes, 0, newBytes, 0, this._length);
        this._bytes = newBytes;
    }
    
    public int length() {
        return this._length;
    }
    
    public byte[] data() {
        return this._bytes;
    }
    
    public void setLength(final int newLength) {
        if (newLength >= 0) {
            this._length = newLength;
            return;
        }
        throw new IndexOutOfBoundsException();
    }
    
    public ByteString toByteString() {
        return new ByteString(this._bytes, 0, this._length);
    }
    
    public byte[] getBytesAndReset() {
        final byte[] buf = new byte[this._length];
        System.arraycopy(this._bytes, 0, buf, 0, this._length);
        this._length = 0;
        return buf;
    }
    
    @Override
    public String toString() {
        try {
            return new String(this._bytes, 0, this._length, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            return new String(this._bytes, 0, this._length);
        }
    }
}
