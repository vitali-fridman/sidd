// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.rmi;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.Serializable;
import java.rmi.server.RMIClientSocketFactory;

public class ConfigurableRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
{
    public static final InetAddress ANY_ADDRESS;
    public static final int DEFAULT_TIMEOUT = -1;
    private static final String UNSPECIFIED_SERVER_HOST = "0.0.0.0";
    private static final long serialVersionUID = 1837252064558539489L;
    private final int _timeout;
    private final InetSocketAddress _bindAddress;
    private final String _serverHost;
    
    public ConfigurableRMIClientSocketFactory() {
        this(-1);
    }
    
    public ConfigurableRMIClientSocketFactory(final int timeout) {
        this(ConfigurableRMIClientSocketFactory.ANY_ADDRESS, timeout);
    }
    
    public ConfigurableRMIClientSocketFactory(final InetAddress bindAddress, final int timeout) {
        this(bindAddress, "0.0.0.0", timeout);
    }
    
    public ConfigurableRMIClientSocketFactory(final InetAddress bindAddress, final String serverHost, final int timeout) {
        this._bindAddress = new InetSocketAddress(bindAddress, 0);
        this._timeout = timeout;
        this._serverHost = serverHost;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        final Socket socket = new Socket();
        socket.bind(this._bindAddress);
        if (this._timeout == -1) {
            socket.connect(new InetSocketAddress(this.getServerHost(host), port));
        }
        else {
            socket.setSoTimeout(this._timeout);
            socket.connect(new InetSocketAddress(this.getServerHost(host), port), this._timeout);
        }
        return socket;
    }
    
    private String getServerHost(final String host) {
        return this._serverHost.equals("0.0.0.0") ? host : this._serverHost;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConfigurableRMIClientSocketFactory)) {
            return false;
        }
        final ConfigurableRMIClientSocketFactory other = (ConfigurableRMIClientSocketFactory)obj;
        return this._timeout == other._timeout && this._bindAddress.equals(other._bindAddress) && this._serverHost.equals(other._serverHost);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode *= 19 + this._timeout;
        hashCode *= 19 + this._bindAddress.hashCode();
        hashCode *= 19 + this._serverHost.hashCode();
        return hashCode;
    }
    
    static {
        ANY_ADDRESS = null;
    }
}
