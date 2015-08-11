// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.Serializable;

public final class RamIndexResult implements Serializable
{
    private static final long serialVersionUID = -9155176473896889938L;
    private static final long KB = 1024L;
    private final int _bucketSize;
    private final long _elapsedTime;
    private final int _fileCount;
    private final int _perIndexMemoryKb;
    private final RamIndexStatistics _statistics;
    private final int _termSize;
    
    RamIndexResult(final int requiredMemoryKb, final int numberOfSubindices, final int termSize, final int bucketSize, final RamIndexStatistics statistics, final long elapsedTime) {
        this._bucketSize = bucketSize;
        this._fileCount = numberOfSubindices;
        this._perIndexMemoryKb = requiredMemoryKb;
        this._termSize = termSize;
        this._statistics = statistics;
        this._elapsedTime = elapsedTime;
    }
    
    public int bucketSize() {
        return this._bucketSize;
    }
    
    public long elapsedTime() {
        return this._elapsedTime;
    }
    
    public int fileCount() {
        return this._fileCount;
    }
    
    public int termSize() {
        return this._termSize;
    }
    
    public int perIndexMemoryKb() {
        return this._perIndexMemoryKb;
    }
    
    public long size() {
        return this._perIndexMemoryKb * 1024L * this._fileCount;
    }
    
    public RamIndexStatistics statistics() {
        return this._statistics;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("Index files created: ").append(this._fileCount).append('\n');
        stringBuilder.append("Memory required to load: ").append(this._perIndexMemoryKb * this._fileCount).append("K\n");
        stringBuilder.append("Term size: ").append(this._termSize).append('\n');
        stringBuilder.append(this._statistics);
        return stringBuilder.toString();
    }
}
