// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.rmi.RemoteException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class LocalRmiObject extends UnicastRemoteObject
{
    protected LocalRmiObject() throws RemoteException {
        super(0, null, new LocalServerSocketFactory());
    }
}
