// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.syslog;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Syslog
{
    private String _syslogHost;
    private int _syslogPort;
    private String _loggerName;
    private InetAddress _syslogAddress;
    private DatagramSocket _sendingSocket;
    
    public Syslog(final String syslogHost, final int syslogPort, final String loggerName) throws SyslogException {
        this._syslogHost = syslogHost;
        this._syslogPort = syslogPort;
        this._loggerName = loggerName;
        this.initSockets(this._syslogHost);
    }
    
    public void syslog(final int facility, final int level, final String message) throws SyslogException {
        final SyslogMessage syslogMessage = new SyslogMessage(facility, level, this._loggerName, message);
        final byte[] payload = syslogMessage.getBytes();
        final DatagramPacket packet = new DatagramPacket(payload, payload.length, this._syslogAddress, this._syslogPort);
        try {
            this._sendingSocket.send(packet);
        }
        catch (IOException e) {
            throw new SyslogException("Error sending syslog", e);
        }
    }
    
    private void initSockets(final String syslogHost) throws SyslogException {
        try {
            this._syslogAddress = InetAddress.getByName(syslogHost);
            this._sendingSocket = new DatagramSocket();
        }
        catch (UnknownHostException e) {
            throw new SyslogException("Can't find host " + syslogHost, e);
        }
        catch (SocketException e2) {
            throw new SyslogException("Can't create sending socket. ", e2);
        }
    }
}
