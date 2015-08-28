// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import com.vontu.profiles.common.ProfilesException;
import java.io.File;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;

public interface DataSourceIndexCreatorResult
{
    public static final String PREINDEX_FILE_NAME_SUFFIX = ".pdx";
    public static final String PREINDEX_FILE_NAME_PREFIX = "ExternalDataSource.";
    
    String cryptoKeyAlias();
    
    long size();
    
    int fileCount();
    
    double estimateFalsePositiveRate(int p0, int p1) throws IllegalArgumentException;
    
    void postCreationStage(DatabaseProfileDescriptor p0, File p1) throws ProfilesException;
    
    String toString();
    
    int getProcessedRowCount();
    
    int getInvalidRowCount();
}
