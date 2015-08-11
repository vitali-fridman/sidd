// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer;

import com.vontu.profileindexer.database.DataSourceStatistics;

public final class DataSourceCorruptedException extends IndexerException
{
    private DataSourceStatistics _statistics;
    
    public DataSourceCorruptedException(final IndexerError error, final Object[] parameters, final DataSourceStatistics statistics) {
        super(error, parameters);
        this._statistics = statistics;
    }
    
    public DataSourceStatistics getStatistics() {
        return this._statistics;
    }
}
