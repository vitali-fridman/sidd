// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.profiles.monitor.document.DocumentIndexFilenameTemplate;
import com.vontu.profiles.monitor.database.DatabaseIndexFilenameTemplate;
import com.vontu.model.data.IndexedInfoSource;

public class IndexDescriptorFactory
{
    public static IndexDescriptor newIndexDescriptor(final IndexedInfoSource indexedInfoSource) {
        return newIndexDescriptor(indexedInfoSource.getInfoSource().getType(), indexedInfoSource);
    }
    
    private static IndexDescriptor newIndexDescriptor(final int type, final IndexedInfoSource indexedInfoSource) {
        if (type == 1) {
            return new DatabaseIndexDescriptor(indexedInfoSource, new DatabaseIndexFilenameTemplate());
        }
        if (type == 2) {
            return new IndexDescriptor(indexedInfoSource, new DocumentIndexFilenameTemplate());
        }
        if (type == 3) {
            return new DatabaseIndexDescriptor(indexedInfoSource, new DatabaseIndexFilenameTemplate());
        }
        throw new IllegalArgumentException("Unrecognized InfoSource type: " + type);
    }
}
