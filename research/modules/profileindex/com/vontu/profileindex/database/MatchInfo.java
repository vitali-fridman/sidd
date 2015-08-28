// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.Arrays;
import com.vontu.profileindex.IndexException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import com.vontu.detection.condition.BasicDatabaseMatchCondition;
import java.util.List;

public class MatchInfo
{
    private List<DatabaseRowMatch> _matches;
    private SearchContent _searchContent;
    private BasicDatabaseMatchCondition _matchCondition;
    
    public MatchInfo(final List<DatabaseRowMatch> matches, final SearchContent searchContent, final BasicDatabaseMatchCondition matchCondition) {
        this._matches = matches;
        this._searchContent = searchContent;
        this._matchCondition = matchCondition;
    }
    
    List<DatabaseRowMatch> getMatches() {
        return Collections.unmodifiableList((List<? extends DatabaseRowMatch>)this._matches);
    }
    
    SearchContent getSearchContent() {
        return this._searchContent;
    }
    
    BasicDatabaseMatchCondition getMatchCondition() {
        return this._matchCondition;
    }
    
    DatabaseMatchConditionViolation createConditionViolation(final int maxMatchCount) {
        final DatabaseMatchConditionViolation result = new DatabaseMatchConditionViolation();
        final Iterator<DatabaseRowMatch> it = this._matches.iterator();
        while (it.hasNext() && result.matchCount() <= maxMatchCount) {
            final DatabaseRowMatch match = it.next();
            result.addMatch(match, this._searchContent);
        }
        return result;
    }
    
    protected void removeAllRowsBut(final boolean[] matched) {
        for (int i = matched.length - 1; i >= 0; --i) {
            if (!matched[i]) {
                this._matches.remove(i);
            }
        }
    }
    
    List<SearchTerm[]> createSearchTermsWithOperands(final SearchTerm[] operandSearchTerms) {
        final List<SearchTerm[]> result = new ArrayList<SearchTerm[]>();
        for (final DatabaseRowMatch match : this.getMatches()) {
            final SearchTerm[] searchTerm = this.createSeachTerms(match, operandSearchTerms);
            result.add(searchTerm);
        }
        return result;
    }
    
    private SearchTerm[] createSeachTerms(final DatabaseRowMatch match, final SearchTerm[] operandSearchTerms) {
        final SearchTerm[] searchTerms = new SearchTerm[operandSearchTerms.length + match.getTokenIds().length];
        System.arraycopy(operandSearchTerms, 0, searchTerms, 0, operandSearchTerms.length);
        ConditionPredicate.setMatchedSearchTerms(this._searchContent.getSearchTermIterator(match.getTokenIds()), operandSearchTerms.length, searchTerms);
        return searchTerms;
    }
    
    int[] setupThresholds() {
        final List<DatabaseRowMatch> matches = this.getMatches();
        final int[] result = new int[matches.size()];
        for (int i = 0; i < matches.size(); ++i) {
            final DatabaseRowMatch match = matches.get(i);
            result[i] = match.getTokenIds().length + 1;
        }
        return result;
    }
    
    DatabaseSearchConditionWithPredicate createMultiColumnPredicateSearchCondition(final BasicDatabaseMatchCondition matchCondition, final int[] columns) {
        return new DatabaseSearchConditionWithPredicate(matchCondition, 0, DatabaseSearchCondition.createColumnMask(columns));
    }
    
    boolean[] matchRowsWithPredicates(final int[] columns, final SearchTerm[] operandSearchTerms, final DatabaseIndex index) throws IndexException {
        final SearchCondition conditionWithPredicate = this.createMultiColumnPredicateSearchCondition(this.getMatchCondition(), columns);
        final List<SearchTerm[]> searchTerms = this.createSearchTermsWithOperands(operandSearchTerms);
        return index.validateRows(searchTerms.toArray(new SearchTerm[searchTerms.size()][]), this.setupThresholds(), conditionWithPredicate);
    }
    
    boolean[] getInitialRows() {
        final boolean[] rowsToKeep = new boolean[this.getMatches().size()];
        Arrays.fill(rowsToKeep, true);
        return rowsToKeep;
    }
}
