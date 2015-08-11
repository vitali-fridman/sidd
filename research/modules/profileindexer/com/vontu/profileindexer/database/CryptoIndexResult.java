// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import java.io.File;

public final class CryptoIndexResult
{
    private final String _cryptoKeyAlias;
    private final long _elapsedTime;
    private final File _indexFile;
    private final DataSourceStatistics _statistics;
    
    CryptoIndexResult(final File indexFile, final String cryptoKeyAlias, final DataSourceStatistics statistics, final long elapsedTime) {
        this._indexFile = indexFile;
        this._cryptoKeyAlias = cryptoKeyAlias;
        this._statistics = statistics;
        this._elapsedTime = elapsedTime;
    }
    
    public String cryptoKeyAlias() {
        return this._cryptoKeyAlias;
    }
    
    public long elapsedTime() {
        return this._elapsedTime;
    }
    
    public File indexFile() {
        return this._indexFile;
    }
    
    public DataSourceStatistics statistics() {
        return this._statistics;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringBuilder = new StringBuffer(String.valueOf(this._statistics));
        stringBuilder.append("\nCryptographic key used: ").append(this._cryptoKeyAlias);
        return stringBuilder.toString();
    }
    
    public int getProcessedRowCount() {
        return this._statistics.rowCount();
    }
    
    public int getInvalidRowCount() {
        return this._statistics.invalidRowCount();
    }
}
