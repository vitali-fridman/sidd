// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.compression;

import java.util.zip.InflaterInputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.Deflater;
import java.io.InputStream;

public class InputStreamCompressionDecorator
{
    public InputStream compress(final InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return new DeflaterInputStream(inputStream, new Deflater(1));
    }
    
    public InputStream decompress(final InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        return new InflaterInputStream(inputStream);
    }
}
