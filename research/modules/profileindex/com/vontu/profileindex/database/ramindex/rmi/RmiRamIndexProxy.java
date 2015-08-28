// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.rmi;

import com.vontu.util.process.JavaChildProcessCommand;
import com.vontu.util.DisposedException;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchCondition;
import com.vontu.profileindex.database.SearchTerm;
import com.vontu.util.process.LocalRmiProcessProxy;
import java.rmi.RemoteException;
import com.vontu.profileindex.database.ramindex.Configuration;
import com.vontu.util.ProtectException;
import java.io.IOException;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.config.SettingReader;
import com.vontu.util.process.ChildProcessProxy;
import java.util.logging.Logger;
import com.vontu.profileindex.database.ramindex.DbRowHit;
import com.vontu.util.Disposable;
import com.vontu.profileindex.database.DatabaseIndex;
import com.vontu.util.DisposableImpl;

public final class RmiRamIndexProxy extends DisposableImpl implements DatabaseIndex, Disposable
{
    public static final String PROCESS_STARTUP_TIMEOUT = "process_startup_timeout";
    public static final int PROCESS_STARTUP_TIMEOUT_DEFAULT = 60000;
    public static final String PROCESS_SHUTDOWN_TIMEOUT = "process_shutdown_timeout";
    public static final int PROCESS_SHUTDOWN_TIMEOUT_DEFAULT = 5000;
    public static final String RMI_REGISRTY_PORT = "rmi_registry_port";
    private static final DbRowHit[] NO_MATCHES;
    private static final Logger _logger;
    private final String _name;
    private int _outstandingSearchCount;
    private final ChildProcessProxy _process;
    private final RmiRamIndex _ramIndex;
    private final SettingReader _settings;
    
    public RmiRamIndexProxy(final SettingProvider settingProvider, final String name, final InputStreamFactory inputStreamFactory, final long indexSize) throws IndexException {
        this._outstandingSearchCount = 0;
        assert name != null && !"".equals(name);
        assert inputStreamFactory != null && !"".equals(inputStreamFactory);
        assert indexSize > 1048576L;
        this._settings = new SettingReader(settingProvider, RmiRamIndexProxy._logger);
        final LocalRmiProcessProxy process = createProcesProxy(name, this._settings.getIntSetting("rmi_registry_port"), indexSize, this._settings.getMemorySetting("minimum_memory_reserve", 209715200L));
        try {
            process.launch();
        }
        catch (IOException e) {
            throw new IndexException(IndexError.INDEX_PROCESS_LAUNCH_ERROR, name, e);
        }
        try {
            this._ramIndex = (RmiRamIndex)process.connect(this._settings.getIntSetting("process_startup_timeout", 60000));
        }
        catch (ProtectException e2) {
            process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_CONNECTION_ERROR, name, (Throwable)e2);
        }
        try {
            this._ramIndex.load(new Configuration(settingProvider), inputStreamFactory);
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
    
    public boolean[] validateRows(final SearchTerm[][] searchTermsWithOperands, final int[] adjustThreshold, final SearchCondition condition) throws IndexException {
        this.enterSearch();
        try {
            if (this.isDisposed()) {
                return new boolean[searchTermsWithOperands.length];
            }
            if (!this._process.isRunning()) {
                return new boolean[searchTermsWithOperands.length];
            }
            return this._ramIndex.validateRows(searchTermsWithOperands, adjustThreshold, condition);
        }
        catch (RemoteException e) {
            this._process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_ERROR, this._name, e);
        }
        finally {
            this.leaveSearch();
        }
    }
    
    public DatabaseRowMatch[] findMatches(final SearchTerm[] terms, final int columnMask, final int threshold, final int[] exceptionTuples, final int minMatches, final boolean isInputTabular) throws IndexException, DisposedException {
        this.enterSearch();
        try {
            if (this.isDisposed()) {
                return RmiRamIndexProxy.NO_MATCHES;
            }
            if (!this._process.isRunning()) {
                return RmiRamIndexProxy.NO_MATCHES;
            }
            return this._ramIndex.findMatches(terms, columnMask, threshold, exceptionTuples, minMatches, isInputTabular);
        }
        catch (RemoteException e) {
            this._process.destroy();
            throw new IndexException(IndexError.INDEX_PROCESS_LOOKUP_ERROR, this._name, e);
        }
        finally {
            this.leaveSearch();
        }
    }
    
    private static LocalRmiProcessProxy createProcesProxy(final String name, final int registryPort, final long indexSize, final long memoryReserve) {
        return new LocalRmiProcessProxy("RI:" + name, getCommand(name, indexSize, memoryReserve, registryPort), registryPort, name);
    }
    
    private static String[] getCommand(final String name, final long indexSize, final long memoryReserve, final int registryPort) {
        final int maxMemory = (int)((indexSize + memoryReserve) / 1048576L);
        return JavaChildProcessCommand.create(RmiRamIndexProcess.class.getName(), new String[] { name, String.valueOf(registryPort) }, System.getProperty("com.vontu.ramindex.logging.properties"), new String[] { "-Xms" + maxMemory + 'M', "-Xmx" + maxMemory + 'M' });
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
        NO_MATCHES = new DbRowHit[0];
        _logger = Logger.getLogger(RmiRamIndexProxy.class.getName());
    }
}
