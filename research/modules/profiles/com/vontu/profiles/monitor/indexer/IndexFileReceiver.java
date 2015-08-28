// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.indexer;

import com.vontu.communication.dataflow.ShipmentReceipt;
import com.vontu.profiles.common.RejectionReason;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import com.vontu.communication.dataflow.UnstructuredDataMetaData;
import java.util.Map;
import com.vontu.communication.dataflow.FileReceiver;

public class IndexFileReceiver extends FileReceiver
{
    private final FileFactory _fileFactory;
    private final Map<UnstructuredDataMetaData, File> _targetFileMap;
    
    public IndexFileReceiver(final File targetFolder) throws IOException {
        this(targetFolder, new FileFactory(targetFolder));
    }
    
    IndexFileReceiver(final File targetFolder, final FileFactory fileFactory) throws IOException {
        super(targetFolder);
        this._targetFileMap = Collections.synchronizedMap(new HashMap<UnstructuredDataMetaData, File>());
        this._fileFactory = fileFactory;
    }
    
    public int newFileShipmentAvailable(final UnstructuredDataMetaData metadata) {
        final String targetFileName = metadata.getDataIdentification();
        final File targetFile = this._fileFactory.getTargetFile(targetFileName);
        if (targetFile.exists()) {
            if (targetFile.length() == metadata.getDataLength()) {
                return RejectionReason.FILE_EXISTS.value();
            }
            if (!targetFile.delete()) {
                return RejectionReason.DELETE_FAILED.value();
            }
        }
        this._targetFileMap.put(metadata, targetFile);
        final File tempFile = this._fileFactory.getTempFile(targetFileName);
        tempFile.delete();
        metadata.setDataIdentification(tempFile.getName());
        return super.newFileShipmentAvailable(metadata);
    }
    
    public int shipmentReceived(final UnstructuredDataMetaData metadata, final ShipmentReceipt receipt, final String filePath) {
        final File targetFile = this._targetFileMap.remove(metadata);
        targetFile.delete();
        if (this._fileFactory.getFile(filePath).renameTo(targetFile)) {
            return super.shipmentReceived(metadata, receipt, filePath);
        }
        return RejectionReason.RENAME_FAILED.value();
    }
}
