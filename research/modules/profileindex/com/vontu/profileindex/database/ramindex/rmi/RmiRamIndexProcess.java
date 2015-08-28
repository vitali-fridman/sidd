// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.rmi;

import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.rmi.Remote;
import java.util.logging.Logger;
import com.vontu.util.process.LocalRmiProcess;

final class RmiRamIndexProcess extends LocalRmiProcess
{
    private static final Logger _logger;
    
    private RmiRamIndexProcess(final String[] args) throws IllegalArgumentException, NumberFormatException {
        super(args, RmiRamIndexProcess._logger);
    }
    
    public static void main(final String[] args) {
        final LocalRmiProcess process = new RmiRamIndexProcess(args);
        process.start();
    }
    
    protected Remote createRmiObject() throws RemoteException {
        return new RmiRamIndexImpl(this._shutdownHandler, this._rmiName, (Unreferenced)this);
    }
    
    static {
        _logger = Logger.getLogger(RmiRamIndexProcess.class.getName());
    }
}
