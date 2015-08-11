// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.net.InetAddress;
import java.rmi.server.RMIServerSocketFactory;

public class LocalServerSocketFactory implements RMIServerSocketFactory
{
    private final InetAddress _bindAddress;
    
    public LocalServerSocketFactory() {
        this._bindAddress = getLoopbackAdapter();
    }
    
    public static InetAddress getLoopbackAdapter() {
        try {
            return InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
        }
        catch (UnknownHostException impossible) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, impossible);
        }
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        return new ServerSocket(port, 0, this._bindAddress);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof LocalServerSocketFactory;
    }
    
    @Override
    public int hashCode() {
        return this._bindAddress.hashCode();
    }
}
