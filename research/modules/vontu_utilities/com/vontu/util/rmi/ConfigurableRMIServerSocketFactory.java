// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.rmi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.rmi.server.RMIServerSocketFactory;

public class ConfigurableRMIServerSocketFactory implements RMIServerSocketFactory
{
    public static final InetAddress ANY_ADDRESS;
    private InetAddress _bindAddress;
    
    public ConfigurableRMIServerSocketFactory(final InetAddress bindAddress) {
        this._bindAddress = ConfigurableRMIServerSocketFactory.ANY_ADDRESS;
        this._bindAddress = bindAddress;
    }
    
    public ConfigurableRMIServerSocketFactory() {
        this._bindAddress = ConfigurableRMIServerSocketFactory.ANY_ADDRESS;
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port, 0, this._bindAddress);
        return socket;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean equal = false;
        if (this == obj) {
            equal = true;
        }
        else if (obj instanceof ConfigurableRMIServerSocketFactory) {
            final ConfigurableRMIServerSocketFactory serverSocketFactory = (ConfigurableRMIServerSocketFactory)obj;
            if (this._bindAddress == serverSocketFactory._bindAddress) {
                equal = true;
            }
            else if (this._bindAddress != null && this._bindAddress.equals(serverSocketFactory._bindAddress)) {
                equal = true;
            }
        }
        return equal;
    }
    
    @Override
    public int hashCode() {
        if (this._bindAddress != null) {
            return this._bindAddress.hashCode();
        }
        return -1;
    }
    
    static {
        ANY_ADDRESS = null;
    }
}
