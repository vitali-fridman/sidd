// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.rmi;

import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.rmi.Remote;
import java.util.logging.Logger;
import com.vontu.util.process.LocalRmiProcess;

final class RmiDocIndexProcess extends LocalRmiProcess
{
    private static final Logger _logger;
    
    private RmiDocIndexProcess(final String[] args) throws IllegalArgumentException, NumberFormatException {
        super(args, RmiDocIndexProcess._logger);
    }
    
    public static void main(final String[] args) {
        final LocalRmiProcess process = new RmiDocIndexProcess(args);
        process.start();
    }
    
    protected Remote createRmiObject() throws RemoteException {
        return new RmiDocIndexImpl(this._shutdownHandler, this._rmiName, (Unreferenced)this);
    }
    
    static {
        _logger = Logger.getLogger(RmiDocIndexProcess.class.getName());
    }
}
