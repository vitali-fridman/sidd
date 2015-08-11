// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.util.process.ParentProcessMonitor;
import java.util.logging.Handler;
import com.vontu.profileindexer.IndexerException;
import java.util.logging.Level;
import com.vontu.profileindexer.IndexerError;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import com.vontu.util.process.LoggerConfigWatcher;
import java.util.logging.Logger;

public final class IndexerProcess
{
    static final int NORMAL_EXIT_CODE = 0;
    static final int ERROR_EXIT_CODE = -1;
    static final int ERROR_EXIT_CODE_WILL_RETRY = -2;
    static final int ERROR_EXIT_CODE_NON_ENOUGH_DISK_SPACE = -3;
    static final int INTERRUPTED_EXIT_CODE = 65296;
    static int exitCode;
    private static final Logger _logger;
    
    public static void main(final String[] args) throws Exception {
        final LoggerConfigWatcher loggerConfigWatcher = new LoggerConfigWatcher();
        loggerConfigWatcher.start();
        final ObjectInputStream inputStream = new ObjectInputStream(System.in);
        final Configuration configuration = (Configuration)inputStream.readObject();
        final CryptoIndexDescriptor indexDescriptor = (CryptoIndexDescriptor)inputStream.readObject();
        final File rdxFile = (File)inputStream.readObject();
        final IndexCreator indexCreator = new IndexCreator(configuration);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("OutOfProcessIndexer JVM is shutting down with exit code value: " + IndexerProcess.exitCode);
            }
        });
        final Thread shutdownListener = (Thread)new ParentProcessWatcher(Thread.currentThread());
        shutdownListener.setPriority(10);
        shutdownListener.start();
        try {
            final RamIndexResult rir = indexCreator.createRamIndex(indexDescriptor, rdxFile);
            final ObjectOutputStream outputStream = new ObjectOutputStream(System.out);
            outputStream.flush();
            outputStream.writeObject(rir);
            outputStream.flush();
        }
        catch (IndexerException e) {
            if (e.getError() == IndexerError.RAM_INDEX_CREATE_ERROR_WILL_RETRY) {
                IndexerProcess._logger.log(Level.SEVERE, "Known EOFException in creating rdx files. Will retry entire job.", (Throwable)e);
                IndexerProcess.exitCode = -2;
            }
            else if (e.getError() == IndexerError.NOT_ENOUGH_DISK_SPACE) {
                IndexerProcess._logger.log(Level.SEVERE, "There is not enough space on the disk to create index files.", (Throwable)e);
                IndexerProcess.exitCode = -3;
            }
            else {
                IndexerProcess._logger.log(Level.SEVERE, "Failed to read or write index files.", (Throwable)e);
                IndexerProcess.exitCode = -1;
            }
        }
        catch (InterruptedException e2) {
            IndexerProcess._logger.info("The indexer has been interrupted.");
            IndexerProcess.exitCode = 65296;
        }
        System.err.println("OutOfProcessIndexer is exiting with exit code value: " + IndexerProcess.exitCode);
        IndexerProcess._logger.info("OutOfProcess Indexer process is exiting with exit code value: " + IndexerProcess.exitCode);
        flushLogger(IndexerProcess._logger);
        System.exit(IndexerProcess.exitCode);
    }
    
    private static void flushLogger(final Logger logger) {
        final Handler[] arr$;
        final Handler[] handlers = arr$ = logger.getHandlers();
        for (final Handler h : arr$) {
            h.flush();
        }
    }
    
    static {
        IndexerProcess.exitCode = 0;
        _logger = Logger.getLogger(IndexerProcess.class.getName());
    }
    
    private static final class ParentProcessWatcher extends ParentProcessMonitor
    {
        private final Thread _indexerThread;
        
        private ParentProcessWatcher(final Thread indexerThread) {
            this._indexerThread = indexerThread;
        }
        
        protected void parentDisappeared() {
            ParentProcessWatcher._logger.warning("Shutting down because the parent process has either written something to this stdin or disappeared.");
            flushLogger(ParentProcessWatcher._logger);
            this._indexerThread.interrupt();
        }
    }
}
