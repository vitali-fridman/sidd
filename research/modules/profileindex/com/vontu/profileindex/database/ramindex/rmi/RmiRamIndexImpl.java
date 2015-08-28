// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.rmi;

import com.vontu.profileindex.database.ramindex.RamIndex;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.database.ramindex.Configuration;
import com.vontu.profileindex.database.SearchCondition;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchTerm;
import java.rmi.RemoteException;
import com.vontu.util.process.RmiProcessShutdownHandler;
import com.vontu.profileindex.database.DatabaseIndex;
import java.util.logging.Logger;
import java.rmi.server.Unreferenced;
import com.vontu.util.process.LocalRmiObject;

final class RmiRamIndexImpl extends LocalRmiObject implements RmiRamIndex, Unreferenced
{
    private static final Logger _logger;
    private static final int _NORMAL_SHUTDOWN_DELAY = 100;
    private volatile boolean _isLoaded;
    private DatabaseIndex _ramIndex;
    private final String _rmiName;
    private final RmiProcessShutdownHandler _shutdownHandler;
    private final Unreferenced _unreferenced;
    
    RmiRamIndexImpl(final RmiProcessShutdownHandler shutdownHandler, final String rmiName, final Unreferenced unreferenced) throws RemoteException {
        this._isLoaded = false;
        this._shutdownHandler = shutdownHandler;
        this._rmiName = rmiName;
        this._unreferenced = unreferenced;
        RmiRamIndexImpl._logger.info("RAM index object " + this._rmiName + " is created.");
    }
    
    public DatabaseRowMatch[] findMatches(final SearchTerm[] terms, final int columns, final int threshold, final int[] exceptionTuples, final int minMatches, final boolean isInputTabular) throws IndexException {
        if (!this._isLoaded) {
            throw new IllegalStateException("Index must be loaded before being used in search.");
        }
        return this._ramIndex.findMatches(terms, columns, threshold, exceptionTuples, minMatches, isInputTabular);
    }
    
    public boolean[] validateRows(final SearchTerm[][] searchTermsWithOperands, final int[] adjustThreshold, final SearchCondition condition) throws RemoteException, IndexException {
        if (!this._isLoaded) {
            throw new IllegalStateException("Index must be loaded before being used in search.");
        }
        return this._ramIndex.validateRows(searchTermsWithOperands, adjustThreshold, condition);
    }
    
    public void load(final Configuration configuration, final InputStreamFactory inputStreamFactory) throws IndexException {
        if (this._isLoaded) {
            throw new IllegalStateException("Index can be loaded only once.");
        }
        this._ramIndex = new RamIndex(configuration, inputStreamFactory);
        this._isLoaded = true;
        RmiRamIndexImpl._logger.info("Loaded RAM index file " + inputStreamFactory + " into RMI object " + this._rmiName + ".");
    }
    
    public void dispose() throws RemoteException {
        RmiRamIndexImpl._logger.fine("Shutdown requrest received.");
        this._shutdownHandler.shutdown(100);
    }
    
    public void unreferenced() {
        this._unreferenced.unreferenced();
    }
    
    static {
        _logger = Logger.getLogger(RmiRamIndexImpl.class.getName());
    }
}
