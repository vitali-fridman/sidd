// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.stream;

import java.io.IOException;
import java.io.Closeable;

public class StreamCloser
{
    public static void closeQuitely(final Closeable item) {
        if (null != item) {
            try {
                item.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void close(final Closeable item) throws IOException {
        if (null != item) {
            item.close();
        }
    }
}
