// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.rmi.Naming;
import com.vontu.util.concurrent.Latch;
import java.util.logging.Logger;

public final class RmiProcessShutdownHandler
{
    private static final Logger _logger;
    private final String _remoteObjectName;
    private volatile boolean _isShuttingDown;
    
    public RmiProcessShutdownHandler(final String remoteObjectName) {
        this._isShuttingDown = false;
        this._remoteObjectName = remoteObjectName;
    }
    
    public void checkShuttingDown() throws ShuttingDownException {
        if (this._isShuttingDown) {
            throw new ShuttingDownException("The process is shutting down.");
        }
    }
    
    public void shutdown(final int delay) throws RemoteException {
        if (this._isShuttingDown) {
            return;
        }
        this._isShuttingDown = true;
        final Latch shutdownLatch = new Latch();
        new Thread("Shutdown handler") {
            @Override
            public void run() {
                try {
                    shutdownLatch.acquire();
                    Thread.sleep(delay);
                }
                catch (InterruptedException ex) {}
                RmiProcessShutdownHandler._logger.info("RMI object " + RmiProcessShutdownHandler.this._remoteObjectName + " is unloaded.");
                System.exit(0);
            }
        }.start();
        try {
            Naming.unbind(this._remoteObjectName);
            RmiProcessShutdownHandler._logger.info("RMI object " + this._remoteObjectName + " is unregistered.");
        }
        catch (MalformedURLException e) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, e);
        }
        catch (NotBoundException e2) {
            RmiProcessShutdownHandler._logger.warning("RMI object " + this._remoteObjectName + " wasn't registered.");
        }
        finally {
            shutdownLatch.release();
        }
    }
    
    static {
        _logger = Logger.getLogger(RmiProcessShutdownHandler.class.getName());
    }
}
