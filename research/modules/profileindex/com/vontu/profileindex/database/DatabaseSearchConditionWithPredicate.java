// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.detection.condition.DatabaseMatchCondition;
import com.vontu.detection.condition.BasicDatabaseMatchCondition;
import java.io.Serializable;

final class DatabaseSearchConditionWithPredicate implements SearchCondition, Serializable
{
    private final int _columnMask;
    private final String _profileId;
    private final int[] _exceptionTuples;
    private int _adjustedThreshold;
    
    public DatabaseSearchConditionWithPredicate(final BasicDatabaseMatchCondition condition, final int matchedTokensCount, final int predicateColumnMask) {
        this._adjustedThreshold = matchedTokensCount + 1;
        this._columnMask = (DatabaseSearchCondition.createColumnMask(condition.columns()) | predicateColumnMask);
        this._profileId = condition.profileId();
        this._exceptionTuples = DatabaseSearchCondition.createExcludedTupleMasks(condition.excludedTuples());
    }
    
    static DatabaseSearchConditionWithPredicate createMultiColumnPredicateSearchCondition(final BasicDatabaseMatchCondition condition, final int[] columns) {
        return new DatabaseSearchConditionWithPredicate(condition, 0, DatabaseSearchCondition.createColumnMask(columns));
    }
    
    public DatabaseSearchConditionWithPredicate(final DatabaseMatchCondition condition, final int matchedTokensCount) {
        this._adjustedThreshold = matchedTokensCount + 1;
        this._columnMask = createColumnMask(condition);
        this._profileId = condition.profileId();
        this._exceptionTuples = DatabaseSearchCondition.createExcludedTupleMasks(condition.excludedTuples());
    }
    
    private static int createColumnMask(final DatabaseMatchCondition condition) {
        return DatabaseSearchCondition.createColumnMask(condition.columns()) | 1 << condition.predicate().column() - 1;
    }
    
    @Override
    public int getColumnMask() {
        return this._columnMask;
    }
    
    @Override
    public String getInfoSourceId() {
        return this._profileId;
    }
    
    @Override
    public int[] getExceptionTuples() {
        return this._exceptionTuples;
    }
    
    @Override
    public int getMinimumMatches() {
        return 1;
    }
    
    @Override
    public int getThreshold() {
        return this._adjustedThreshold;
    }
}
