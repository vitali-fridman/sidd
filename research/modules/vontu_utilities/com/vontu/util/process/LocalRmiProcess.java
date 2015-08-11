// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.util.logging.Level;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.io.UnsupportedEncodingException;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.net.URLEncoder;
import com.vontu.util.config.ConfigurationException;
import java.util.logging.Logger;
import java.rmi.server.Unreferenced;

public abstract class LocalRmiProcess implements Unreferenced
{
    private final Logger _logger;
    private final LoggerConfigWatcher _loggerConfigWatcher;
    private final ParentProcessMonitor _parentProcessMonitor;
    protected final String _rmiName;
    protected final RmiProcessShutdownHandler _shutdownHandler;
    
    protected LocalRmiProcess(final String[] args, final Logger logger) throws ConfigurationException, IllegalArgumentException, NumberFormatException {
        this._logger = logger;
        this._loggerConfigWatcher = new LoggerConfigWatcher();
        if (args.length < 2 || args[0].length() == 0) {
            throw new IllegalArgumentException("Expected RMI ojbect name and RMI registry port arguments.");
        }
        this._rmiName = createRmiName(args[0], Integer.parseInt(args[1]));
        this._shutdownHandler = new RmiProcessShutdownHandler(this._rmiName);
        this._parentProcessMonitor = new ParentProcessWatcher();
    }
    
    private static String createRmiName(final String objectName, final int registryPort) {
        try {
            return "//127.0.0.1:" + registryPort + "/" + URLEncoder.encode(objectName, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, e);
        }
    }
    
    protected abstract Remote createRmiObject() throws RemoteException;
    
    public void start() {
        this._loggerConfigWatcher.start();
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            Naming.bind(this._rmiName, this.createRmiObject());
            this._logger.info("RMI object " + this._rmiName + " is registered.");
            this._parentProcessMonitor.start();
        }
        catch (Exception e) {
            this._logger.log(Level.SEVERE, "Failed to register RMI object \"" + this._rmiName + "\".", e);
        }
    }
    
    protected void terminate(final int delay) {
        try {
            this._shutdownHandler.shutdown(delay);
        }
        catch (RemoteException e) {
            this._logger.log(Level.SEVERE, "Failed to unbind RMI object " + this._rmiName + " from the registry.");
        }
        this._loggerConfigWatcher.stop();
    }
    
    @Override
    public void unreferenced() {
        this._logger.warning("RMI object " + this._rmiName + " will be unloaded because it isn't being used.");
        try {
            this._shutdownHandler.shutdown(0);
        }
        catch (RemoteException e) {
            this._logger.log(Level.WARNING, "Exception occurred while shutting down RMI object " + this._rmiName + '.', e);
        }
    }
    
    protected final class ParentProcessWatcher extends ParentProcessMonitor
    {
        @Override
        protected void parentDisappeared() {
            ParentProcessWatcher._logger.warning("Shutting down the process because its parent process is gone.");
            LocalRmiProcess.this.terminate(0);
        }
    }
}
