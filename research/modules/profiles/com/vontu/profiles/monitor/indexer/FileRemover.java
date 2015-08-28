// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.util.Iterator;
import com.vontu.monitor.communication.configset.Effect;
import com.vontu.monitor.communication.configset.ItemChange;
import com.vontu.monitor.communication.configset.ConfigSourceChange;
import com.vontu.profiles.common.IndexFilenameTemplate;
import com.vontu.logging.SystemEventWriter;
import com.vontu.logging.LocalLogWriter;
import com.vontu.messaging.Registry;
import com.vontu.profiles.common.ProfileType;
import java.io.File;
import java.util.regex.Pattern;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import com.vontu.monitor.communication.configset.ConfigSourceObserver;

public class FileRemover implements ConfigSourceObserver<IndexDescriptorMarshallable>
{
    private static final Logger _logger;
    private final Executor _executor;
    private final Pattern _fileMask;
    private final File _indexFolder;
    private final DeleteResultLogger _eventLogger;
    
    public FileRemover(final Executor executor, final File indexFolder, final ProfileType profileType) {
        this(executor, indexFolder, profileType, LocalLogWriter.createAggregated(FileRemover._logger, Registry.getSystemEventWriter()));
    }
    
    public FileRemover(final Executor executor, final File indexFolder, final ProfileType profileType, final SystemEventWriter eventWriter) {
        this(executor, indexFolder, profileType.indexFilenameTemplate(), new DeleteResultLogger(profileType, eventWriter));
    }
    
    public FileRemover(final Executor executor, final File indexFolder, final IndexFilenameTemplate filenameTemplate, final DeleteResultLogger eventLogger) {
        this._executor = executor;
        this._eventLogger = eventLogger;
        this._indexFolder = indexFolder;
        this._fileMask = filenameTemplate.createFileMask();
    }
    
    public void configSourceChanged(final Iterable<IndexDescriptorMarshallable> configSource, final ConfigSourceChange<IndexDescriptorMarshallable> change) {
        for (final ItemChange<IndexDescriptorMarshallable> indexChange : change.effects()) {
            if (indexChange.effect() == Effect.REMOVED) {
                this._executor.execute(new DeleteFilesRequest((IndexDescriptorMarshallable)indexChange.affectedItem(), this._eventLogger, this._indexFolder));
            }
        }
    }
    
    public void configSourceInitialized(final Iterable<IndexDescriptorMarshallable> configSource) {
        this._executor.execute(new DeleteOrphanedFilesRequest(configSource, this._eventLogger, this._indexFolder, this._fileMask));
    }
    
    static {
        _logger = Logger.getLogger(FileRemover.class.getName());
    }
}
