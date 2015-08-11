// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.compression;

import com.vontu.util.stream.StreamReader;
import java.io.ByteArrayInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class VontuCompressor
{
    public static final byte[] COMPRESSION_FLAG;
    public static final int COMPRESSION_THRESHOLD = 100;
    
    public static byte[] compress(final byte[] original) throws VontuCompressionException {
        if (original == null || original.length <= 100 || isCompressed(original)) {
            return original;
        }
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream(original.length);
            final OutputStream deflaterOutput = getCompressorStream(output);
            deflaterOutput.write(original, 0, original.length);
            deflaterOutput.flush();
            output.flush();
            deflaterOutput.close();
            return output.toByteArray();
        }
        catch (IOException e) {
            throw new VontuCompressionException("Cannot compress data", e);
        }
    }
    
    public static CompressorOutputStream getCompressorStream(final OutputStream outputStream) throws IOException {
        outputStream.write(VontuCompressor.COMPRESSION_FLAG);
        return new CompressorOutputStream(outputStream);
    }
    
    public static InputStream getDecompressingStream(final InputStream inputStream) throws IOException {
        if (null == inputStream) {
            return null;
        }
        final BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        bufferedStream.mark(VontuCompressor.COMPRESSION_FLAG.length + 1);
        final byte[] compressionFlag = new byte[VontuCompressor.COMPRESSION_FLAG.length];
        bufferedStream.read(compressionFlag);
        if (isCompressed(compressionFlag)) {
            return new InflaterInputStream(bufferedStream);
        }
        bufferedStream.reset();
        return bufferedStream;
    }
    
    public static byte[] decompress(final byte[] compressed) throws VontuCompressionException {
        if (compressed == null || !isCompressed(compressed)) {
            return compressed;
        }
        final Inflater inflater = new Inflater();
        final InflaterInputStream input = new InflaterInputStream(new ByteArrayInputStream(compressed, VontuCompressor.COMPRESSION_FLAG.length, compressed.length - VontuCompressor.COMPRESSION_FLAG.length), inflater);
        try {
            return StreamReader.read(input);
        }
        catch (IOException e) {
            throw new VontuCompressionException("Unable to decompress data", e);
        }
        finally {
            inflater.end();
        }
    }
    
    public static boolean isCompressed(final byte[] compressed) {
        if (null == compressed || compressed.length < VontuCompressor.COMPRESSION_FLAG.length) {
            return false;
        }
        for (int i = 0; i < VontuCompressor.COMPRESSION_FLAG.length; ++i) {
            if (compressed[i] != VontuCompressor.COMPRESSION_FLAG[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        COMPRESSION_FLAG = new byte[] { 120, 45, 118, 111, 110, 116, 117, 45, 99, 111, 109, 112 };
    }
}
