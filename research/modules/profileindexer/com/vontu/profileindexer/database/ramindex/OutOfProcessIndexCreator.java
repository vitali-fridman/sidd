// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.util.process.ProcessStateMonitor;
import java.nio.channels.ReadableByteChannel;
import com.vontu.util.ProtectRuntimeException;
import com.vontu.util.ProtectError;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.channels.ClosedByInterruptException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.IOException;
import com.vontu.profileindexer.IndexerException;
import com.vontu.profileindexer.IndexerError;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import com.vontu.util.OutputRedirector;
import com.vontu.util.process.JvmStdErrorCheckedReader;
import java.util.logging.Level;
import com.vontu.util.process.JavaChildProcessCommand;
import java.io.File;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;

public final class OutOfProcessIndexCreator implements RamIndexCreator
{
    public static final String CLASSPATH = "indexer_classpath";
    public static final String CLASSPATH_DEFAULT;
    public static final String SHUTDOWN_TIMEOUT = "indexer_shutdown_timeout";
    public static final int SHUTDOWN_TIMEOUT_DEFAULT = 15000;
    public static final String LOGGING_CONFIG_FILE = "indexer_logging_config_file";
    private static final int MB = 1048576;
    private static final Logger _logger;
    private final Configuration _configuration;
    private final SettingProvider _settingProvider;
    private final SettingReader _settingReader;
    private static final int SLEEP_TIMEOUT = 1000;
    private static final int MAX_TIMEOUTS = 10;
    
    public OutOfProcessIndexCreator(final SettingProvider settingProvider) {
        this._configuration = new Configuration(settingProvider);
        this._settingReader = new SettingReader(settingProvider, OutOfProcessIndexCreator._logger);
        this._settingProvider = settingProvider;
    }
    
    private String getClassPath() {
        return this._settingReader.getSetting("indexer_classpath", OutOfProcessIndexCreator.CLASSPATH_DEFAULT);
    }
    
    private String getJvmMaxHeapSize() {
        return String.valueOf(this._configuration.getMaxIndexerMemory() / 1048576L) + 'M';
    }
    
    private String getLoggingConfigFilePath() {
        return this._settingProvider.getSetting("indexer_logging_config_file");
    }
    
    private int getShutdownTimeout() {
        return this._settingReader.getIntSetting("indexer_shutdown_timeout", 15000);
    }
    
    @Override
    public RamIndexResult createRamIndex(final CryptoIndexDescriptor indexDescriptor, final File rdxFile) throws IndexerException, InterruptedException {
        final File idxFile = indexDescriptor.indexFile();
        String[] gvmArgs;
        if (!this._configuration.enableRemoteDebugging()) {
            gvmArgs = new String[] { "-Xms64M", "-Xmx" + this.getJvmMaxHeapSize() };
        }
        else {
            gvmArgs = new String[] { "-Xms64M", "-Xmx" + this.getJvmMaxHeapSize(), "-Xdebug", "-Xrunjdwp:transport=dt_socket,address=5025,server=y,suspend=n" };
        }
        final String[] command = JavaChildProcessCommand.create(IndexerProcess.class.getName(), new String[0], this.getLoggingConfigFilePath(), gvmArgs, this.getClassPath());
        if (OutOfProcessIndexCreator._logger.isLoggable(Level.FINE)) {
            final StringBuilder sb = new StringBuilder();
            for (final String c : command) {
                sb.append(c);
                sb.append(' ');
            }
            OutOfProcessIndexCreator._logger.fine("Launching process: " + (Object)sb);
        }
        try {
            final Process indexer = Runtime.getRuntime().exec(command);
            final JvmStdErrorCheckedReader indexerErrorOut = new JvmStdErrorCheckedReader(indexer.getErrorStream());
            final OutputRedirector stderrRedirector = new OutputRedirector((BufferedReader)indexerErrorOut, "INDEXER", (OutputStream)System.err);
            stderrRedirector.start();
            ObjectOutputStream parameterStream = null;
            try {
                parameterStream = new ObjectOutputStream(indexer.getOutputStream());
                parameterStream.flush();
                parameterStream.writeObject(this._configuration);
                parameterStream.writeObject(indexDescriptor);
                parameterStream.writeObject(rdxFile);
                parameterStream.flush();
            }
            catch (IOException e) {
                OutOfProcessIndexCreator._logger.log(Level.SEVERE, "OutOfProcessIndexer JVM failed to start.", e);
                int count = 0;
                while (!indexerErrorOut.gotInput() && count++ < 10) {
                    Thread.sleep(1000L);
                }
                if (count == 10) {
                    OutOfProcessIndexCreator._logger.log(Level.SEVERE, "Did not get any output from failed OutOfProcessIndexer JVM");
                    throw new IndexerException(IndexerError.PROCESS_IO_ERROR, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e);
                }
                if (indexerErrorOut.gotMemoryError()) {
                    throw new IndexerException(IndexerError.NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e);
                }
                throw new IndexerException(IndexerError.GENERIC_COULD_NOT_CREATE_JVM, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e);
            }
            ByteBuffer buffer = null;
            try {
                final ReadableByteChannel channel = Channels.newChannel(indexer.getInputStream());
                buffer = ByteBuffer.allocate(8192);
                while (channel.read(buffer) >= 0) {}
            }
            catch (ClosedByInterruptException e2) {
                OutOfProcessIndexCreator._logger.log(Level.FINE, "Got ClosedByInterruptException while reading from OutOfProcessIndexCreator stdout.", e2);
                this.interruptExternalIndexer(parameterStream, indexer, stderrRedirector, e2);
            }
            try {
                indexer.waitFor();
            }
            catch (InterruptedException e3) {
                OutOfProcessIndexCreator._logger.log(Level.SEVERE, "Got InterruptedException while waiting for OutOfProcessIndexCreator to exit.", e3);
                throw new IndexerException(IndexerError.RAM_INDEX_CREATE_ERROR, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            final int indexerExitValue = indexer.exitValue();
            if (indexerExitValue == 0) {
                buffer = (ByteBuffer)buffer.flip();
                if (this._configuration.enableRemoteDebugging()) {
                    readDebuggerOutput(buffer);
                }
                final ObjectInputStream resultStream = new ObjectInputStream(new ByteBufferInputStream(buffer));
                return (RamIndexResult)resultStream.readObject();
            }
            if (indexerExitValue == -2) {
                throw new IndexerException(IndexerError.RAM_INDEX_CREATE_ERROR_WILL_RETRY, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            if (indexerExitValue == -3) {
                throw new IndexerException(IndexerError.NOT_ENOUGH_DISK_SPACE, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            if (indexerExitValue == -1) {
                throw new IndexerException(IndexerError.RAM_INDEX_CREATE_ERROR, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            if (indexerExitValue == 65296) {
                throw new InterruptedException("External Indexer process was interrupted while indexing.");
            }
            OutOfProcessIndexCreator._logger.severe("OutOfProcess Indexer exited with unexpected exit code value: " + indexerExitValue + " Most likely unable to start JVM due to memory setting.");
            new OutputRedirector(indexer.getInputStream(), "INDEXER").start();
            if (!indexerErrorOut.gotInput()) {
                OutOfProcessIndexCreator._logger.severe("OutOfProcess Indexer did not produce any sterr output up to this point.");
                throw new IndexerException(IndexerError.GENERIC_COULD_NOT_CREATE_JVM, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            if (indexerErrorOut.gotMemoryError()) {
                OutOfProcessIndexCreator._logger.severe("OutOfProcess Indexer reported memory allocation error for JVM heap.");
                throw new IndexerException(IndexerError.NOT_ENOUGH_SPACE_FOR_OBJECT_HEAP, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            if (indexerErrorOut.gotGenericJvmStartError()) {
                OutOfProcessIndexCreator._logger.severe("OutOfProcess Indexer reported generic JVM start error.");
                throw new IndexerException(IndexerError.GENERIC_COULD_NOT_CREATE_JVM, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
            }
            OutOfProcessIndexCreator._logger.severe("OutOfProcess Indexer produced stderr output which is not recognized.");
            throw new IndexerException(IndexerError.GENERIC_COULD_NOT_CREATE_JVM, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() });
        }
        catch (ClassNotFoundException e4) {
            throw new ProtectRuntimeException(ProtectError.UNEXPECTED_ERROR, (Throwable)e4);
        }
        catch (IOException e5) {
            throw new IndexerException(IndexerError.PROCESS_IO_ERROR, new String[] { rdxFile.getAbsolutePath(), idxFile.getAbsolutePath() }, e5);
        }
    }
    
    private static void readDebuggerOutput(final ByteBuffer buffer) {
        while (buffer.hasRemaining() && buffer.get() != 10) {}
    }
    
    private void interruptExternalIndexer(final ObjectOutputStream parameterStream, final Process indexer, final OutputRedirector stderrRedirector, final Exception e) throws InterruptedException, IndexerException {
        OutOfProcessIndexCreator._logger.fine("Will attempt to interrupt OutOfProcessIndexer.");
        try {
            parameterStream.write(0);
            parameterStream.flush();
            OutOfProcessIndexCreator._logger.fine("Wrote one byte to OutOfProcessIndexer stdin stream.");
        }
        catch (IOException e2) {
            OutOfProcessIndexCreator._logger.log(Level.FINE, "Got IOException trying to write one byte to OutOfProcessIndexer stdin stream.", e);
        }
        Thread.interrupted();
        final ProcessStateMonitor stateMonitor = new ProcessStateMonitor(indexer);
        stateMonitor.start();
        OutOfProcessIndexCreator._logger.fine("Started ProcessStateMonitor for OutOfProcessIndexer; shutdown timeout is: " + this.getShutdownTimeout());
        if (!stateMonitor.waitForShutdown(this.getShutdownTimeout())) {
            OutOfProcessIndexCreator._logger.fine("OutOfProcessIndexer did not shutdown in time - will destroy.");
            stderrRedirector.detach();
            indexer.destroy();
            OutOfProcessIndexCreator._logger.fine("Attempted to kill OutOfProcessIndexer.");
            throw new IndexerException(IndexerError.PROCESS_SHUTDOWN_ERROR);
        }
        OutOfProcessIndexCreator._logger.fine("OutOfProcessIndexer has shutdown before timeout expired.");
        throw new InterruptedException("OutOfProcessIndexCreator Got " + e.toString());
    }
    
    static {
        CLASSPATH_DEFAULT = System.getProperty("java.class.path");
        _logger = Logger.getLogger(OutOfProcessIndexCreator.class.getName());
    }
    
    class ByteBufferInputStream extends InputStream
    {
        private ByteBuffer buffer;
        
        public ByteBufferInputStream(final ByteBuffer buffer) {
            this.buffer = buffer;
        }
        
        public void setByteBuffer(final ByteBuffer buffer) {
            this.buffer = buffer;
        }
        
        @Override
        public int available() {
            return this.buffer.remaining();
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public int read() {
            if (this.buffer.hasRemaining()) {
                return this.buffer.get() & 0xFF;
            }
            return -1;
        }
        
        @Override
        public int read(final byte[] b) {
            return this.read(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int offset, int length) {
            if (!this.buffer.hasRemaining()) {
                return -1;
            }
            if (length > this.buffer.remaining()) {
                length = this.buffer.remaining();
            }
            this.buffer.get(b, offset, length);
            return length;
        }
    }
}
