// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.Reader;

public class CharSequenceReader extends Reader
{
    private static final int NOTSET = -1;
    private CharSequence _charSeq;
    private int _index;
    private int _mark;
    private int _markInvalid;
    
    public CharSequenceReader(final CharSequence charSeq) {
        this._index = 0;
        this._mark = -1;
        this._markInvalid = 0;
        if (charSeq == null) {
            throw new IllegalArgumentException("CharSequence cannot be null");
        }
        this._charSeq = charSeq;
    }
    
    @Override
    public int read(final char[] cbuf, final int off, int len) {
        if (this._charSeq == null) {
            throw new IllegalStateException("Cannot read after close");
        }
        if (this._index == this._charSeq.length()) {
            return -1;
        }
        if (this._index + len > this._charSeq.length()) {
            len = this._charSeq.length() - this._index;
        }
        int read;
        if (this._charSeq instanceof CharArrayCharSequence) {
            read = ((CharArrayCharSequence)this._charSeq).copy(this._index, cbuf, off, len);
        }
        else {
            this._charSeq.subSequence(this._index, this._index + len).toString().getChars(0, len, cbuf, off);
            read = len;
        }
        this._index += len;
        return read;
    }
    
    @Override
    public long skip(long n) {
        if (this._index + n > this._charSeq.length()) {
            n = this._charSeq.length() - this._index;
        }
        this._index += (int)n;
        return n;
    }
    
    @Override
    public void close() {
        this._charSeq = null;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void mark(final int readAhead) {
        this._mark = this._index;
        this._markInvalid = this._index + readAhead;
    }
    
    @Override
    public void reset() {
        if (this._mark == -1) {
            this._index = 0;
        }
        else if (this._index <= this._markInvalid) {
            this._index = this._mark;
        }
        else {
            this._index = 0;
            this._mark = -1;
        }
    }
    
    @Override
    public boolean ready() {
        return true;
    }
}
