// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.profileindexer.database.DataSourceStatistics;
import com.vontu.profileindexer.database.CryptoIndexResult;
import java.io.File;
import java.io.Serializable;

public final class CryptoIndexDescriptor implements Serializable
{
    private static final long serialVersionUID = -1162128832581212470L;
    private final File _indexFile;
    private final int _rowCount;
    private final int _cellCount;
    
    public CryptoIndexDescriptor(final File cryptoIndexFile, final int rowCount, final int cellCount) {
        this._indexFile = cryptoIndexFile;
        this._rowCount = rowCount;
        this._cellCount = cellCount;
    }
    
    public CryptoIndexDescriptor(final CryptoIndexResult indexResult) {
        this(indexResult.indexFile(), indexResult.statistics());
    }
    
    private CryptoIndexDescriptor(final File indexFile, final DataSourceStatistics statistics) {
        this(indexFile, statistics.rowCount(), statistics.cellCount());
    }
    
    int cellCount() {
        return this._cellCount;
    }
    
    File indexFile() {
        return this._indexFile;
    }
    
    int rowCount() {
        return this._rowCount;
    }
    
    @Override
    public String toString() {
        return this._indexFile.getAbsolutePath() + " Rows: " + this._rowCount + " Cells: " + this._cellCount;
    }
}
