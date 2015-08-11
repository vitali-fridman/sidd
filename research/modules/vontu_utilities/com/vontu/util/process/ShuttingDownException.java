// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.rmi.RemoteException;

public class ShuttingDownException extends RemoteException
{
    public ShuttingDownException() {
    }
    
    public ShuttingDownException(final String s) {
        super(s);
    }
    
    public ShuttingDownException(final String s, final Throwable ex) {
        super(s, ex);
    }
}
