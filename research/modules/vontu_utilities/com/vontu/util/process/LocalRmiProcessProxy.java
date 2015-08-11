// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.io.IOException;
import java.util.logging.Level;
import java.lang.reflect.InvocationTargetException;
import com.vontu.util.ProtectException;
import java.util.concurrent.Callable;
import com.vontu.util.concurrent.ProgressiveBackoff;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Naming;
import java.io.UnsupportedEncodingException;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.net.URLEncoder;
import com.vontu.util.RemoteDisposable;
import java.util.logging.Logger;

public final class LocalRmiProcessProxy extends ChildProcessProxy
{
    private static final Logger _logger;
    private static final int WAIT_BEFORE_CONNECT = 1000;
    private final String _name;
    private RemoteDisposable _rmiObject;
    private Thread _shutdownThread;
    
    public LocalRmiProcessProxy(final String logPrefix, final String[] command, final int registryPort, final String objectName) {
        super(logPrefix, command);
        try {
            this._name = "//127.0.0.1:" + registryPort + '/' + URLEncoder.encode(objectName, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, e);
        }
    }
    
    private void cleanupRegistry() {
        try {
            final Remote staleObject = Naming.lookup(this._name);
            if (staleObject instanceof RemoteDisposable) {
                try {
                    ((RemoteDisposable)staleObject).dispose();
                }
                catch (Exception e) {
                    LocalRmiProcessProxy._logger.fine("Failed to shut down stale RMI object " + this._name + ". " + e.getMessage());
                }
            }
            Naming.unbind(this._name);
            LocalRmiProcessProxy._logger.info("RMI object " + this._name + " is unregistered.");
        }
        catch (NotBoundException e3) {
            LocalRmiProcessProxy._logger.fine("RMI object " + this._name + " is not bound. Nothing to clean up.");
        }
        catch (MalformedURLException impossible) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, impossible);
        }
        catch (RemoteException e2) {
            LocalRmiProcessProxy._logger.warning("Failed to check for stale RMI object. " + e2.getMessage());
        }
    }
    
    public synchronized Remote connect(final int timeout) throws ProtectException {
        if (this._rmiObject != null) {
            throw new IllegalStateException("Already connected.");
        }
        final GetRemoteObjectProc getRemoteObjectProc = new GetRemoteObjectProc(this._name);
        final ProgressiveBackoff progressiveBackoff = new ProgressiveBackoff(getRemoteObjectProc, 1000);
        final long startTime = System.currentTimeMillis();
        try {
            if (!progressiveBackoff.call(timeout)) {
                this.destroy();
                throw new ProtectException(ProtectError.RMI_ERROR, "Remote object " + this._name + " in process " + this.getShortName() + " didn't respond within " + timeout / 1000 + " seconds.");
            }
        }
        catch (InterruptedException e2) {
            this.destroy();
            throw new ProtectException(ProtectError.RMI_ERROR, "The current thread was interrupted while connecting to remote object " + this._name + " in process " + this.getShortName() + '.');
        }
        catch (InvocationTargetException e) {
            this.destroy();
            throw new ProtectException(ProtectError.RMI_ERROR, e.getCause(), "Unable to connect to remote object " + this._name + " in process " + this.getShortName() + '.');
        }
        this._rmiObject = (RemoteDisposable)getRemoteObjectProc.getRemoteObject();
        if (LocalRmiProcessProxy._logger.isLoggable(Level.FINE)) {
            LocalRmiProcessProxy._logger.fine("Successfully connected to remote object " + this._name + " in process " + this.getShortName() + " in " + String.valueOf(System.currentTimeMillis() - startTime) + " milliseconds.");
        }
        return this._rmiObject;
    }
    
    @Override
    public void launch() throws IOException {
        this.cleanupRegistry();
        super.launch();
    }
    
    @Override
    public synchronized void destroy() {
        super.destroy();
        try {
            Naming.unbind(this._name);
        }
        catch (NotBoundException ignore) {
            LocalRmiProcessProxy._logger.fine("RMI object " + this._name + " is not bound.");
        }
        catch (RemoteException ignore2) {
            LocalRmiProcessProxy._logger.fine("Failed to connect to " + this._name + " for unbind.");
        }
        catch (Exception e) {
            LocalRmiProcessProxy._logger.log(Level.SEVERE, "Failed to unbind failed RMI object " + this._name + '.', e);
        }
        this._rmiObject = null;
        if (this._shutdownThread != null && this._shutdownThread.isAlive()) {
            this._shutdownThread.setPriority(1);
            this._shutdownThread.interrupt();
        }
    }
    
    @Override
    protected synchronized void requestShutdown() throws ProtectException {
        if (this._rmiObject == null) {
            throw new ProtectException(ProtectError.RMI_ERROR, "Not connected to RMI object.");
        }
        (this._shutdownThread = new Thread(new ShutdownRequest(this._rmiObject, this._name, this), "Shutdown requestor for " + this._name)).setDaemon(true);
        this._shutdownThread.start();
    }
    
    @Override
    public synchronized boolean shutdown(final int timeoutMsecs) {
        final boolean success = super.shutdown(timeoutMsecs);
        this._rmiObject = null;
        return success;
    }
    
    static {
        _logger = Logger.getLogger(LocalRmiProcessProxy.class.getName());
    }
    
    private static final class GetRemoteObjectProc implements Callable<Boolean>
    {
        private final String _name;
        private Remote _remoteObject;
        
        GetRemoteObjectProc(final String name) {
            this._name = name;
        }
        
        @Override
        public Boolean call() throws Exception {
            try {
                this._remoteObject = Naming.lookup(this._name);
            }
            catch (NotBoundException e) {
                return false;
            }
            return true;
        }
        
        Remote getRemoteObject() {
            return this._remoteObject;
        }
    }
    
    private static final class ShutdownRequest implements Runnable
    {
        private final String _name;
        private final ChildProcessProxy _process;
        private final RemoteDisposable _rmiObject;
        
        ShutdownRequest(final RemoteDisposable rmiObject, final String name, final ChildProcessProxy process) {
            this._process = process;
            this._rmiObject = rmiObject;
            this._name = name;
        }
        
        @Override
        public void run() {
            try {
                this._rmiObject.dispose();
            }
            catch (Exception e) {
                if (LocalRmiProcessProxy._logger.isLoggable(Level.FINE)) {
                    LocalRmiProcessProxy._logger.log(Level.WARNING, "RMI object " + this._name + " failed to accept the shutdown request.", e);
                }
                else {
                    LocalRmiProcessProxy._logger.log(Level.WARNING, "RMI object " + this._name + " failed to accept the shutdown request.");
                }
                this._process.destroy();
            }
        }
    }
}
