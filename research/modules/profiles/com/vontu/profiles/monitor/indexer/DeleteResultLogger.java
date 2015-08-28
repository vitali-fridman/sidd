// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import java.io.File;
import com.vontu.profiles.common.FilenameCollection;
import com.vontu.logging.SystemEventLogger;
import com.vontu.logging.event.system.SystemEventSeverity;
import com.vontu.communication.data.IndexDescriptorMarshallable;
import java.util.Collection;
import com.vontu.logging.SystemEventWriter;
import com.vontu.profiles.common.ProfileType;
import com.vontu.logging.SystemEvent;

public class DeleteResultLogger
{
    private static final SystemEvent LISTING_FAILED_EVENT;
    private final ProfileType _profileType;
    private final SystemEventWriter _eventWriter;
    
    public DeleteResultLogger(final ProfileType profileType, final SystemEventWriter eventWriter) {
        this._profileType = profileType;
        this._eventWriter = eventWriter;
    }
    
    private String getEventId(final String shortName) {
        return "com.vontu.profiles." + this._profileType.value() + '.' + shortName;
    }
    
    public void logFilesDeleted(final Collection<String> fileNames, final IndexDescriptorMarshallable index) {
        final String eventType = (fileNames.size() > 1) ? this.getEventId("files_removed") : this.getEventId("one_file_removed");
        final SystemEventLogger event = new SystemEventLogger(eventType, SystemEventSeverity.INFO, this._eventWriter);
        event.log(new String[] { index.getProfile().getProfileName(), String.valueOf(index.getIndexVersion()), new FilenameCollection(fileNames).toCsv() });
    }
    
    public void logDeleteOrphanFailed(final File file) {
        final SystemEventLogger event = new SystemEventLogger(this.getEventId("orphaned_removal_failed"), SystemEventSeverity.WARNING, this._eventWriter);
        event.log(new String[] { file.getName() });
    }
    
    public void logListingFailed(final File indexFolder) {
        DeleteResultLogger.LISTING_FAILED_EVENT.log(this._eventWriter, new String[] { indexFolder.getAbsolutePath() });
    }
    
    public void logDeleteFailed(final Collection<String> fileNames, final IndexDescriptorMarshallable index) {
        final String eventType = (fileNames.size() > 1) ? this.getEventId("files_removal_failed") : this.getEventId("one_file_removal_failed");
        final SystemEventLogger event = new SystemEventLogger(eventType, SystemEventSeverity.INFO, this._eventWriter);
        event.log(new String[] { index.getProfile().getProfileName(), String.valueOf(index.getIndexVersion()), new FilenameCollection(fileNames).toCsv() });
    }
    
    static {
        LISTING_FAILED_EVENT = new SystemEvent("com.vontu.profiles.index_folder_inaccessible", SystemEventSeverity.SEVERE);
    }
}
