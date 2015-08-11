// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.FilterReader;

public class EchoingReader extends FilterReader
{
    private final Writer _out;
    
    public EchoingReader(final Reader base, final Writer out) {
        super(base);
        this._out = out;
    }
    
    @Override
    public int read() throws IOException {
        final int c = super.read();
        if (c != -1) {
            this._out.write(c);
        }
        return c;
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        final int charsRead = super.read(cbuf, off, len);
        if (charsRead != -1) {
            this._out.write(cbuf, off, charsRead);
        }
        return charsRead;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return super.skip(n);
    }
    
    @Override
    public boolean ready() throws IOException {
        return super.ready();
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        throw new IOException("Mark is not supported.");
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("Reset is not supported.");
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
}
