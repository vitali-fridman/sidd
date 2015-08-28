// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.detection.condition.PredicateOperator;
import com.vontu.profileindex.database.predicate.RecursivePredicateConverter;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.vontu.detection.condition.BasicDatabaseMatchCondition;
import com.vontu.util.Disposable;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.detection.condition.FilteringDatabaseMatchCondition;
import com.vontu.detection.output.ConditionViolation;
import com.vontu.detection.condition.DatabaseMatchCondition;
import com.vontu.security.KeyStorehouseException;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import com.vontu.keystorehouse.KeyContainer;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import com.vontu.util.Stopwatch;
import com.vontu.keystorehouse.KeyStorehouse;
import java.util.logging.Logger;
import com.vontu.profileindex.ProfileIndex;

class DatabaseInfoMatcher implements FilteringDatabaseProfileMatcher, DatabaseProfileMatcher, ProfileIndex
{
    private static final Logger _logger;
    private static final String MAX_MATCH_COUNT_PROPERTY = "maximum_number_of_matches_to_return";
    private static final int MAX_MATCH_COUNT_DEFAULT = 100;
    private final DatabaseIndex _index;
    private final DatabaseProfileIndexDescriptor _indexDescriptor;
    private final KeyStorehouse _keyStorehouse;
    private final int _maxMatchCount;
    private final Stopwatch _stopwatch;
    
    DatabaseInfoMatcher(final DatabaseIndex index, final DatabaseProfileIndexDescriptor indexDescriptor, final KeyStorehouse keyStorehouse, final SettingProvider settings) {
        this(index, indexDescriptor, keyStorehouse, new SettingReader(settings, DatabaseInfoMatcher._logger));
    }
    
    DatabaseInfoMatcher(final DatabaseIndex index, final DatabaseProfileIndexDescriptor indexDescriptor, final KeyStorehouse keyStorehouse, final SettingReader settingReader) {
        this._stopwatch = new Stopwatch("DatabaseInfoMatcher");
        this._index = index;
        this._indexDescriptor = indexDescriptor;
        this._keyStorehouse = keyStorehouse;
        this._maxMatchCount = settingReader.getIntSetting("maximum_number_of_matches_to_return", 100);
    }
    
    private String getCryptoKeyAlias() {
        return this._indexDescriptor.cryptoKeyAlias();
    }
    
    private KeyContainer getKeyContainer() throws IndexException {
        try {
            return this._keyStorehouse.getKeyContainer(this.getCryptoKeyAlias());
        }
        catch (KeyStorehouseException e) {
            throw new IndexException(IndexError.INDEX_CRYPTO_ERROR, this.getCryptoKeyAlias(), (Throwable)e);
        }
    }
    
    @Override
    public ConditionViolation detectViolation(final DatabaseMatchCondition matchCondition, final CryptographicContent content) throws IndexException {
        return this.detectViolation(new DatabaseMatchConditionAdapter(matchCondition), content);
    }
    
    private DatabaseRowMatch[] findMatches(final SearchCondition condition, final SearchTermContent searchTermContent) throws IndexException {
        return this.findMatches(condition, searchTermContent.searchTerms(), searchTermContent.isTabular());
    }
    
    private DatabaseRowMatch[] findMatches(final SearchCondition condition, final SearchTerm[] searchTerms, final boolean isInputTabular) throws IndexException {
        return this._index.findMatches(searchTerms, condition.getColumnMask(), condition.getThreshold(), condition.getExceptionTuples(), condition.getMinimumMatches(), isInputTabular);
    }
    
    @Override
    public ProfileIndexDescriptor getDescriptor() {
        return this._indexDescriptor;
    }
    
    DatabaseIndex getRamIndex() {
        return this._index;
    }
    
    private int getMaxMatchCount() {
        return this._maxMatchCount;
    }
    
    @Override
    public void unload() throws IndexException {
        try {
            if (this._index instanceof Disposable) {
                ((Disposable)this._index).dispose();
            }
        }
        catch (IndexException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new IndexException(IndexError.INDEX_UNLOAD_ERROR, new Object[] { this._indexDescriptor.profile().name(), String.valueOf(this._indexDescriptor.version()) }, t);
        }
    }
    
    @Override
    public ConditionViolation detectViolation(final FilteringDatabaseMatchCondition condition, final CryptographicContent content) throws IndexException {
        final SearchContent searchContent = content.getSearchContent(this.getCryptoKeyAlias());
        final SearchCondition searchCondition = new DatabaseSearchCondition(condition);
        if (DatabaseInfoMatcher._logger.isLoggable(Level.FINE)) {
            this._stopwatch.start();
        }
        final DatabaseRowMatch[] tabularTextMatches = this.findMatches(searchCondition, searchContent.tabularTextContent());
        final DatabaseRowMatch[] freeTextMatches = this.findMatches(searchCondition, searchContent.freeTextContent());
        final List<DatabaseRowMatch> matches = new ArrayList<DatabaseRowMatch>(Arrays.asList(tabularTextMatches));
        matches.addAll(Arrays.asList(freeTextMatches));
        if (matches.size() == 0) {
            return null;
        }
        final ConditionViolation conditionViolation = this.createViolation(matches, searchContent, condition);
        if (DatabaseInfoMatcher._logger.isLoggable(Level.FINE)) {
            DatabaseInfoMatcher._logger.fine("Database index search took " + this._stopwatch.stop().getLastTime() + " milliseconds.");
        }
        return conditionViolation;
    }
    
    private ConditionViolation createViolation(final List<DatabaseRowMatch> matches, final SearchContent searchContent, final FilteringDatabaseMatchCondition matchCondition) throws IndexException {
        final MatchInfo matchInfo = new MatchInfo(matches, searchContent, matchCondition);
        final PredicateOperator predicate = matchCondition.filterExpression();
        if (predicate != null) {
            final PredicateConverter converter = new RecursivePredicateConverter();
            final RowMatcher rowValidator = converter.createMatcher(predicate);
            final RowPredicateMatcher predicateMatcher = new RowPredicateMatcher(new SearchTermFactory(this.getKeyContainer()), this._index);
            final boolean[] rowsToKeep = rowValidator.matchRows(matchInfo, predicateMatcher);
            matchInfo.removeAllRowsBut(rowsToKeep);
        }
        final DatabaseMatchConditionViolation conditionViolation = matchInfo.createConditionViolation(this.getMaxMatchCount());
        if (DatabaseInfoMatcher._logger.isLoggable(Level.FINE)) {
            DatabaseInfoMatcher._logger.fine("Number of matches: " + conditionViolation.matchCount() + ", minimum match threshold: " + matchCondition.minMatchCount() + '.');
        }
        if (conditionViolation.matchCount() == this.getMaxMatchCount()) {
            DatabaseInfoMatcher._logger.info("Exceeded maximum_number_of_matches_to_return of " + this.getMaxMatchCount() + '.');
        }
        return (ConditionViolation)((conditionViolation.matchCount() < matchCondition.minMatchCount()) ? null : conditionViolation);
    }
    
    static {
        _logger = Logger.getLogger(DatabaseInfoMatcher.class.getName());
    }
}
