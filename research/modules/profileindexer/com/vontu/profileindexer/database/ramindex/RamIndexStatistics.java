// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.Serializable;

public final class RamIndexStatistics implements Serializable
{
    private static final long serialVersionUID = 32140729131663215L;
    private int _uncommonTermCellCount;
    private int _commonTermCount;
    private int _commonTermCellCount;
    
    void addCommonTermCellCount(final int count) {
        this._commonTermCellCount += count;
    }
    
    void addCommonTermCount(final int count) {
        this._commonTermCount += count;
    }
    
    void addUncommonTermCount(final int count) {
        this._uncommonTermCellCount += count;
    }
    
    public int commonTermCellCount() {
        return this._commonTermCellCount;
    }
    
    public int commonTermCount() {
        return this._commonTermCount;
    }
    
    public int uncommonTermCellCount() {
        return this._uncommonTermCellCount;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("Uncommon term cells: ").append(this._uncommonTermCellCount).append('\n');
        stringBuilder.append("Common term cells: ").append(this._commonTermCellCount).append('\n');
        stringBuilder.append("Common terms: ").append(this._commonTermCount);
        return stringBuilder.toString();
    }
}
