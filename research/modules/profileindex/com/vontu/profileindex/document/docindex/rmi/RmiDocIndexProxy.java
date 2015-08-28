// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.rmi;

import com.vontu.util.process.JavaChildProcessCommand;
import com.vontu.util.DisposedException;
import com.vontu.logging.operational.api.OperationalLog;
import com.vontu.detection.logging.DetectionOperationLogCode;
import com.vontu.detection.output.DocumentConditionViolation;
import java.rmi.RemoteException;
import com.vontu.profileindex.document.docindex.Configuration;
import com.vontu.util.ProtectException;
import java.io.IOException;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import com.vontu.util.process.LocalRmiProcessProxy;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;
import com.vontu.util.config.SettingReader;
import com.vontu.util.process.ChildProcessProxy;
import com.vontu.util.Disposable;
import com.vontu.profileindex.document.DocIndex;
import com.vontu.util.DisposableImpl;

public final class RmiDocIndexProxy extends DisposableImpl implements DocIndex, Disposable
{
    public static final String PROCESS_STARTUP_TIMEOUT = "process_startup_timeout";
    public static final int PROCESS_STARTUP_TIMEOUT_DEFAULT = 5000;
    public static final String PROCESS_SHUTDOWN_TIMEOUT = "process_shutdown_timeout";
    public static final int PROCESS_SHUTDOWN_TIMEOUT_DEFAULT = 5000;
    public static final String RMI_REGISRTY_PORT = "rmi_registry_port";
    private final String _name;
    private int _outstandingSearchCount;
    private final ChildProcessProxy _process;
    private final RmiDocIndex _docIndex;
    private final SettingReader _settings;
    private static final Logger _logger;
    
    public RmiDocIndexProxy(final SettingProvider settingProvider, final String name, final InputStreamFactory inputStreamFactory, final long indexSize) throws IndexException {
        this._outstandingSearchCount = 0;
        assert name != null && !"".equals(name);
        assert inputStreamFactory != null && !"".equals(inputStreamFactory);
        this._settings = new SettingReader(settingProvider, RmiDocIndexProxy._logger);
        final int localRegistryPort = this._settings.getIntSetting("rmi_registry_port");
        final LocalRmiProcessProxy process = new LocalRmiProcessProxy("RDI:" + name, this.getCommand(name, indexSize, localRegistryPort), localRegistryPort, name);
        try {
            process.launch();
        }
        catch (IOException e) {
            throw new IndexException(IndexError.INDEX_PROCESS_LAUNCH_ERROR, name, e);
        }
        try {
            this._docIndex = (RmiDocIndex)process.connect(this._settings.getIntSetting("process_startup_timeout", 5000));
        }
        catch (ProtectException e2) {
            process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_CONNECTION_ERROR, name, (Throwable)e2);
        }
        try {
            this._docIndex.load(new Configuration(settingProvider), inputStreamFactory);
        }
        catch (RemoteException e3) {
            process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOAD_ERROR, name, e3);
        }
        this._name = name;
        this._process = (ChildProcessProxy)process;
    }
    
    public void dispose() throws Throwable {
        super.dispose();
        this.waitForSearchCompletion();
        this._process.shutdown(this._settings.getIntSetting("process_shutdown_timeout", 5000));
    }
    
    private synchronized void enterSearch() {
        ++this._outstandingSearchCount;
    }
    
    public DocumentConditionViolation findPartialDocMatches(final CharSequence textContent, final int threshold) throws IndexException, DisposedException {
        this.enterSearch();
        try {
            if (this.isDisposed()) {
                return null;
            }
            if (!this._process.isRunning()) {
                return null;
            }
            return this._docIndex.findPartialDocMatches(textContent, threshold);
        }
        catch (RemoteException e) {
            if (e.getCause() instanceof OutOfMemoryError) {
                OperationalLog.logWarning(DetectionOperationLogCode.IDM_PROFILE_OOP_RECOVERABLE_MATCH_ERROR, (Throwable)e, new Object[] { this._name });
                throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_RECOVERABLE_ERROR, this._name, e);
            }
            OperationalLog.logError(DetectionOperationLogCode.IDM_PROFILE_OOP_UNRECOVERABLE_MATCH_ERROR, (Throwable)e, new Object[] { this._name });
            this._process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_ERROR, this._name, e);
        }
        finally {
            this.leaveSearch();
        }
    }
    
    public DocumentConditionViolation findExactDocMatches(final CharSequence textContent) throws IndexException, DisposedException {
        this.enterSearch();
        try {
            if (this.isDisposed()) {
                return null;
            }
            if (!this._process.isRunning()) {
                return null;
            }
            return this._docIndex.findExactDocMatches(textContent);
        }
        catch (RemoteException e) {
            this._process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_ERROR, this._name, e);
        }
        finally {
            this.leaveSearch();
        }
    }
    
    public DocumentConditionViolation findBinaryDocMatches(final byte[] binaryContent) throws IndexException, DisposedException {
        this.enterSearch();
        try {
            if (this.isDisposed()) {
                return null;
            }
            if (!this._process.isRunning()) {
                return null;
            }
            return this._docIndex.findBinaryDocMatches(binaryContent);
        }
        catch (RemoteException e) {
            this._process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_ERROR, this._name, e);
        }
        finally {
            this.leaveSearch();
        }
    }
    
    private String[] getCommand(final String name, final long indexSize, final int registryPort) {
        final int maxMemory = (int)((indexSize + this._settings.getMemorySetting("minimum_memory_reserve", 209715200L)) / 1048576L);
        return JavaChildProcessCommand.create(RmiDocIndexProcess.class.getName(), new String[] { name, String.valueOf(registryPort) }, System.getProperty("com.vontu.ramindex.logging.properties"), new String[] { "-Xms" + maxMemory + 'M', "-Xmx" + maxMemory + 'M' });
    }
    
    private synchronized void leaveSearch() {
        --this._outstandingSearchCount;
        this.notify();
    }
    
    private synchronized void waitForSearchCompletion() throws InterruptedException {
        while (this._outstandingSearchCount > 0) {
            this.wait();
        }
    }
    
    static {
        _logger = Logger.getLogger(RmiDocIndexProxy.class.getName());
    }
}
