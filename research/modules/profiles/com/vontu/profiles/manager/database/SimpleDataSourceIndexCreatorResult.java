// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import java.io.File;
import com.vontu.profileindexer.database.DatabaseProfileDescriptor;
import com.vontu.profileindexer.database.DatabaseIndexCreatorResult;

public class SimpleDataSourceIndexCreatorResult implements DataSourceIndexCreatorResult
{
    DatabaseIndexCreatorResult _result;
    
    public SimpleDataSourceIndexCreatorResult(final DatabaseIndexCreatorResult result) {
        this._result = result;
    }
    
    @Override
    public String cryptoKeyAlias() {
        return this._result.cryptoKeyAlias();
    }
    
    @Override
    public long size() {
        return this._result.size();
    }
    
    @Override
    public int fileCount() {
        return this._result.fileCount();
    }
    
    @Override
    public double estimateFalsePositiveRate(final int columnCount, final int textProximityRadius) throws IllegalArgumentException {
        return this._result.estimateFalsePositiveRate(columnCount, textProximityRadius);
    }
    
    @Override
    public void postCreationStage(final DatabaseProfileDescriptor savedProfile, final File rdxFile) {
    }
    
    @Override
    public String toString() {
        return this._result.toString();
    }
    
    @Override
    public int getProcessedRowCount() {
        return this._result.getProcessedRowCount();
    }
    
    @Override
    public int getInvalidRowCount() {
        return this._result.getInvalidRowCount();
    }
}
