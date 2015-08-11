// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface RemoteDisposable extends Remote
{
    void dispose() throws ProtectException, RemoteException;
}
