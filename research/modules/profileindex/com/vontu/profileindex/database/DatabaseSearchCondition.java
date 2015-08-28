// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.detection.condition.BasicDatabaseMatchCondition;
import java.io.Serializable;

class DatabaseSearchCondition implements SearchCondition, Serializable
{
    private final int _columnMask;
    private final BasicDatabaseMatchCondition _condition;
    private final int[] _excludedTuples;
    
    DatabaseSearchCondition(final BasicDatabaseMatchCondition condition) {
        this._columnMask = createColumnMask(condition.columns());
        this._condition = condition;
        this._excludedTuples = createExcludedTupleMasks(condition.excludedTuples());
    }
    
    public static int createColumnMask(final int[] columns) {
        int columnMask = 0;
        for (int i = 0; i < columns.length; ++i) {
            columnMask |= 1 << columns[i] - 1;
        }
        return columnMask;
    }
    
    public static int[] createExcludedTupleMasks(final int[][] excludedTuples) {
        final int[] excludedTupleMasks = new int[excludedTuples.length];
        for (int i = 0; i < excludedTuples.length; ++i) {
            excludedTupleMasks[i] = createColumnMask(excludedTuples[i]);
        }
        return excludedTupleMasks;
    }
    
    @Override
    public int getColumnMask() {
        return this._columnMask;
    }
    
    @Override
    public String getInfoSourceId() {
        return this._condition.profileId();
    }
    
    @Override
    public int[] getExceptionTuples() {
        return this._excludedTuples;
    }
    
    @Override
    public int getMinimumMatches() {
        return this._condition.minMatchCount();
    }
    
    @Override
    public int getThreshold() {
        return this._condition.columnThreshold();
    }
}
