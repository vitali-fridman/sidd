// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.compression;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class CompressorOutputStream extends DeflaterOutputStream
{
    private final Deflater _deflater;
    
    CompressorOutputStream(final OutputStream outputStream) {
        this(outputStream, new Deflater(1));
    }
    
    CompressorOutputStream(final OutputStream outputStream, final Deflater deflater) {
        super(outputStream, deflater);
        this._deflater = deflater;
    }
    
    @Override
    public void finish() throws IOException {
        super.finish();
        this._deflater.end();
    }
}
