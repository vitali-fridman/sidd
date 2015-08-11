// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.profileindexer.database.ramindex.RamIndexStatistics;
import com.vontu.ramindex.util.AccuracyCalculator;
import com.vontu.profileindexer.database.ramindex.RamIndexResult;
import com.vontu.profileindexer.ProfileIndexerResult;

public final class DatabaseIndexCreatorResult implements ProfileIndexerResult
{
    private final RamIndexResult _ramIndexResult;
    private final CryptoIndexResult _cryptoIndexResult;
    private final long _elapsedTime;
    
    DatabaseIndexCreatorResult(final CryptoIndexResult idxResult, final RamIndexResult rdxResult, final long elapsedTime) {
        this._cryptoIndexResult = idxResult;
        this._ramIndexResult = rdxResult;
        this._elapsedTime = elapsedTime;
    }
    
    public int averageBucketSize() {
        return this._ramIndexResult.bucketSize();
    }
    
    public String cryptoKeyAlias() {
        return this._cryptoIndexResult.cryptoKeyAlias();
    }
    
    public long elapsedTime() {
        return this._elapsedTime;
    }
    
    public double estimateFalsePositiveRate(final int columnCount, final int textProximityRadius) throws IllegalArgumentException {
        if (columnCount < 0 || columnCount > this.getProfileColumnCount()) {
            throw new IllegalArgumentException("Invalid number of columns.");
        }
        return AccuracyCalculator.estimateFalsePositiveRate(this.termSize(), this.averageBucketSize(), this.rowCount(), columnCount, textProximityRadius);
    }
    
    private int getProfileColumnCount() {
        return this.statistics().columnStatistics().length;
    }
    
    @Override
    public int fileCount() {
        return this._ramIndexResult.fileCount();
    }
    
    public DataSourceStatistics statistics() {
        return this._cryptoIndexResult.statistics();
    }
    
    public RamIndexStatistics ramIndexStatistics() {
        return this._ramIndexResult.statistics();
    }
    
    public int rowCount() {
        return this._cryptoIndexResult.statistics().rowCount();
    }
    
    @Override
    public long size() {
        return this._ramIndexResult.size();
    }
    
    public int termSize() {
        return this._ramIndexResult.termSize();
    }
    
    @Override
    public String toString() {
        final StringBuffer stringBuilder = new StringBuffer(String.valueOf(this._cryptoIndexResult));
        stringBuilder.append('\n').append(this._ramIndexResult);
        stringBuilder.append("\nElapsed time: ");
        if (this._elapsedTime > 10000L) {
            stringBuilder.append(this._elapsedTime / 1000L).append(" seconds.");
        }
        else {
            stringBuilder.append(this._elapsedTime).append(" milliseconds.");
        }
        return stringBuilder.toString();
    }
    
    public int getProcessedRowCount() {
        return this._cryptoIndexResult.getProcessedRowCount();
    }
    
    public int getInvalidRowCount() {
        return this._cryptoIndexResult.getInvalidRowCount();
    }
}
