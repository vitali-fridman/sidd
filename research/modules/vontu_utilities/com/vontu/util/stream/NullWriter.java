// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.IOException;
import java.io.Writer;

public class NullWriter extends Writer
{
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
    }
}
