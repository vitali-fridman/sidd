// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class StreamReader
{
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    
    public static void readAndCloseInput(final InputStream input, final OutputStream output) throws IOException {
        read(input, output, true, 4096);
    }
    
    public static void readAndCloseInput(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        read(input, output, true, bufferSize);
    }
    
    public static void read(final InputStream input, final OutputStream output) throws IOException {
        read(input, output, false, 4096);
    }
    
    public static void read(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        read(input, output, false, bufferSize);
    }
    
    public static void read(final InputStream input, final OutputStream output, final boolean closeSource) throws IOException {
        read(input, output, closeSource, 4096);
    }
    
    public static void read(final InputStream input, final OutputStream output, final boolean closeSource, final int bufferSize) throws IOException {
        if (null == input || null == output) {
            return;
        }
        final byte[] buffer = new byte[bufferSize];
        for (int i = input.read(buffer); i > -1; i = input.read(buffer)) {
            output.write(buffer, 0, i);
        }
        if (closeSource) {
            input.close();
        }
    }
    
    public static byte[] read(final InputStream input) throws IOException {
        return read(input, true);
    }
    
    public static byte[] read(final InputStream input, final int bufferSize) throws IOException {
        return read(input, true, bufferSize);
    }
    
    public static byte[] read(final InputStream input, final boolean closeSource) throws IOException {
        return read(input, closeSource, 4096);
    }
    
    public static byte[] read(final InputStream input, final boolean closeSource, final int bufferSize) throws IOException {
        if (null == input) {
            return null;
        }
        final ByteArrayOutputStream destination = new ByteArrayOutputStream();
        read(input, destination, closeSource, bufferSize);
        return destination.toByteArray();
    }
    
    public static byte[] readBytes(final InputStream inputStream, int numBytesToRead) throws IOException {
        final byte[] bytes = new byte[numBytesToRead];
        int bytesReadSoFar = 0;
        do {
            final int numBytesRead = inputStream.read(bytes, bytesReadSoFar, numBytesToRead);
            if (numBytesRead == -1) {
                throw new IOException("End of stream reached unexpectedly.");
            }
            bytesReadSoFar += numBytesRead;
            numBytesToRead -= numBytesRead;
        } while (numBytesToRead > 0);
        return bytes;
    }
}
