// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.logging.SystemEventLogger;
import com.vontu.profileindex.IndexException;
import java.util.logging.Level;
import com.vontu.profiles.monitor.notification.LoadFailure;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.profiles.common.FilenameCollection;
import com.vontu.logging.event.system.SystemEventSeverity;
import com.vontu.profiles.monitor.notification.LoadOperation;
import com.vontu.profiles.monitor.notification.LoadEvent;
import com.vontu.logging.LocalLogWriter;
import com.vontu.messaging.Registry;
import com.vontu.logging.SystemEventWriter;
import java.util.logging.Logger;
import com.vontu.profiles.monitor.notification.LoadObserver;

public class LoadResultLogger implements LoadObserver
{
    private static final Logger _logger;
    private final String _profileType;
    private final SystemEventWriter _logWriter;
    
    public LoadResultLogger(final String profileType) {
        this(profileType, LocalLogWriter.createAggregated(LoadResultLogger._logger, Registry.getSystemEventWriter()));
    }
    
    LoadResultLogger(final String profileType, final SystemEventWriter logWriter) {
        this._profileType = profileType;
        this._logWriter = logWriter;
    }
    
    protected String getEventId(final String shortName) {
        return "com.vontu.profiles." + this._profileType + '.' + shortName;
    }
    
    @Override
    public void loadComplete(final LoadEvent event) {
        final IndexDescriptorMarshallable index = event.getIndex();
        final LoadOperation operation = event.getOperation();
        if (operation == LoadOperation.LOAD_INDEX) {
            this.writeSystemEvent(SystemEventSeverity.INFO, this.getEventId("loaded"), getMessageArguments(index, FilenameCollection.forIndex(index).toCsv()));
        }
        else if (operation == LoadOperation.UNLOAD_INDEX) {
            this.writeSystemEvent(SystemEventSeverity.INFO, this.getEventId("unloaded"), getMessageArguments(index, new String[0]));
        }
    }
    
    private static String[] getMessageArguments(final IndexDescriptorMarshallable index, final String... args) {
        final String[] result = new String[args.length + 2];
        result[0] = index.getProfile().getProfileName();
        result[1] = String.valueOf(index.getIndexVersion());
        System.arraycopy(args, 0, result, 2, args.length);
        return result;
    }
    
    @Override
    public void loadFailed(final LoadFailure failure) {
        final IndexException exception = failure.getException();
        final IndexDescriptorMarshallable index = failure.getIndex();
        final LoadOperation operation = failure.getOperation();
        if (operation == LoadOperation.LOAD_INDEX) {
            this.writeSystemEvent(SystemEventSeverity.SEVERE, this.getEventId("load_failed"), getMessageArguments(index, exception.getMessage()));
            LoadResultLogger._logger.log(Level.SEVERE, "Failed to load profile.", (Throwable)exception);
        }
        else if (operation == LoadOperation.UNLOAD_INDEX) {
            this.writeSystemEvent(SystemEventSeverity.WARNING, this.getEventId("unload_failed"), getMessageArguments(index, exception.getMessage()));
            LoadResultLogger._logger.log(Level.WARNING, "Failed to unload profile.", (Throwable)exception);
        }
    }
    
    private void writeSystemEvent(final SystemEventSeverity severity, final String eventType, final String... parameters) {
        final SystemEventLogger event = new SystemEventLogger(eventType, severity, this._logWriter);
        event.log(parameters);
    }
    
    static {
        _logger = Logger.getLogger(LoadResultLogger.class.getName());
    }
}
