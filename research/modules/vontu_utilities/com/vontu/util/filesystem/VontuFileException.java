// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

public class VontuFileException extends Exception
{
    VontuFileException(final String msg) {
        super(msg);
    }
    
    VontuFileException(final String msg, final Exception e) {
        super(msg, e);
    }
}
