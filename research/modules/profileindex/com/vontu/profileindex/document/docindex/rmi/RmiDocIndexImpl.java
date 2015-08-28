// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.rmi;

import com.vontu.profileindex.document.docindex.DocIndexImpl;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.document.docindex.Configuration;
import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;
import java.rmi.RemoteException;
import com.vontu.util.process.RmiProcessShutdownHandler;
import com.vontu.profileindex.document.DocIndex;
import java.util.logging.Logger;
import java.rmi.server.Unreferenced;
import com.vontu.util.process.LocalRmiObject;

final class RmiDocIndexImpl extends LocalRmiObject implements RmiDocIndex, Unreferenced
{
    private static final Logger _logger;
    private static final int _NORMAL_SHUTDOWN_DELAY = 100;
    private volatile boolean _isLoaded;
    private DocIndex _docIndex;
    private final String _rmiName;
    private final RmiProcessShutdownHandler _shutdownHandler;
    private final Unreferenced _unreferenced;
    
    RmiDocIndexImpl(final RmiProcessShutdownHandler shutdownHandler, final String rmiName, final Unreferenced unreferenced) throws RemoteException {
        this._isLoaded = false;
        this._shutdownHandler = shutdownHandler;
        this._rmiName = rmiName;
        this._unreferenced = unreferenced;
        RmiDocIndexImpl._logger.info("DOC index object " + this._rmiName + " is created.");
    }
    
    public DocumentConditionViolation findPartialDocMatches(final CharSequence textContent, final int threshold) throws IndexException {
        if (!this._isLoaded) {
            throw new IllegalStateException("Index must be loaded before being used in search.");
        }
        return this._docIndex.findPartialDocMatches(textContent, threshold);
    }
    
    public DocumentConditionViolation findExactDocMatches(final CharSequence textContent) throws IndexException {
        if (!this._isLoaded) {
            throw new IllegalStateException("Index must be loaded before being used in search.");
        }
        return this._docIndex.findExactDocMatches(textContent);
    }
    
    public DocumentConditionViolation findBinaryDocMatches(final byte[] binaryContent) throws IndexException {
        if (!this._isLoaded) {
            throw new IllegalStateException("Index must be loaded before being used in search.");
        }
        return this._docIndex.findBinaryDocMatches(binaryContent);
    }
    
    public void load(final Configuration configuration, final InputStreamFactory inputStreamFactory) throws IndexException {
        if (this._isLoaded) {
            throw new IllegalStateException("Index can be loaded only once.");
        }
        this._docIndex = new DocIndexImpl(configuration, inputStreamFactory);
        this._isLoaded = true;
        RmiDocIndexImpl._logger.info("Loaded DOC index file " + inputStreamFactory + " into RMI object " + this._rmiName + ".");
    }
    
    public void dispose() throws RemoteException {
        RmiDocIndexImpl._logger.fine("Shutdown requrest received.");
        this._shutdownHandler.shutdown(100);
    }
    
    public void unreferenced() {
        this._unreferenced.unreferenced();
    }
    
    static {
        _logger = Logger.getLogger(RmiDocIndexImpl.class.getName());
    }
}
