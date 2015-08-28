// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.jflex;

import java.io.IOException;
import java.io.Reader;

public class NewLineEndingReader extends Reader
{
    private Reader _originalReader;
    private boolean _reachedEndOfStream;
    
    public NewLineEndingReader(final Reader reader) {
        this._originalReader = reader;
        this._reachedEndOfStream = false;
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (this._reachedEndOfStream) {
            return -1;
        }
        int r = this._originalReader.read(cbuf, off, len);
        if (r == -1) {
            cbuf[off] = '\n';
            r = 1;
            this._reachedEndOfStream = true;
        }
        return r;
    }
    
    @Override
    public void close() throws IOException {
        this._originalReader.close();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long skipped = this._originalReader.skip(n);
        if (skipped < n) {
            this._reachedEndOfStream = true;
            return skipped + 1L;
        }
        return skipped;
    }
    
    @Override
    public boolean ready() throws IOException {
        return this._originalReader.ready();
    }
    
    @Override
    public boolean markSupported() {
        return this._originalReader.markSupported();
    }
    
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        this._originalReader.mark(readAheadLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this._originalReader.reset();
        this._reachedEndOfStream = false;
    }
}
