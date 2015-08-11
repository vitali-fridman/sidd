// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TabStrippingInputStream extends FilterInputStream
{
    public TabStrippingInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public int read() throws IOException {
        final int c = super.read();
        if (c == 9) {
            return 32;
        }
        return c;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int numRead = super.read(b, off, len);
        for (int i = off; i < off + numRead; ++i) {
            if (b[i] == 9) {
                b[i] = 32;
            }
        }
        return numRead;
    }
}
