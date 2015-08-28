// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.security.KeyStorehouseException;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.IndexError;
import java.util.Iterator;
import com.vontu.nlp.lexer.TabularTokenSet;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.HashMap;
import com.vontu.nlp.lexer.TokenizedContent;
import com.vontu.util.Stopwatch;
import com.vontu.keystorehouse.KeyStorehouse;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

public class CryptographicContent
{
    private static final Logger _logger;
    private static final TokenRange[] NO_RANGES;
    private final Map _cryptoKeySearchContentMap;
    private final Collection _freeTextTokens;
    private final KeyStorehouse _keyStorehouse;
    private final Stopwatch _stopwatch;
    private final TokenizedContent _tokenizedContent;
    private final Collection _tabularTextTokens;
    
    public CryptographicContent(final TokenizedContent tokenizedContent, final KeyStorehouse keyStorehouse) {
        this._cryptoKeySearchContentMap = new HashMap();
        this._stopwatch = new Stopwatch("CryptographicContent");
        final List allTokens = tokenizedContent.getTokens();
        final Collection tokenSets = tokenizedContent.getTokenSets(0);
        final Stopwatch contentSeparationStopwatch = new Stopwatch("Tabular content separation");
        if (CryptographicContent._logger.isLoggable(Level.FINE)) {
            contentSeparationStopwatch.start();
        }
        this._tabularTextTokens = new LinkedList();
        final TokenRange[] tabularRanges = getTabularTokens(tokenSets, this._tabularTextTokens);
        getFreeTextTokens(allTokens, tabularRanges, this._freeTextTokens = new ArrayList(allTokens.size() - this._tabularTextTokens.size()));
        if (CryptographicContent._logger.isLoggable(Level.FINE)) {
            CryptographicContent._logger.fine("Tabular vs. free text token separation took " + contentSeparationStopwatch.stop().getLastTime() + " milliseconds.");
        }
        this._tokenizedContent = tokenizedContent;
        this._keyStorehouse = keyStorehouse;
    }
    
    private static void addFreeTextTokenRange(final List allTokens, final int start, final int end, final Collection freeTextTokens) {
        if (CryptographicContent._logger.isLoggable(Level.FINEST)) {
            CryptographicContent._logger.finest("Free text starts at position " + start + " and ends at position " + end + '.');
        }
        for (int i = start; i < end; ++i) {
            freeTextTokens.add(allTokens.get(i));
        }
    }
    
    private static TokenRange addTabularTokenRange(final TabularTokenSet tokenSet, final Collection tabularTokens) {
        final TokenRange tabularRange = new TokenRange(tokenSet);
        if (CryptographicContent._logger.isLoggable(Level.FINEST)) {
            CryptographicContent._logger.finest("Tabular set start and end tokens: " + tabularRange.start() + " : " + tabularRange.end() + '.');
            CryptographicContent._logger.finest("Tabular set has " + tokenSet.size() + " rows.");
        }
        final Iterator rowIterator = tokenSet.iterator();
        while (rowIterator.hasNext()) {
            addTokensFromRow(rowIterator.next(), tabularTokens);
        }
        return tabularRange;
    }
    
    private static void addTokensFromRow(final Collection rowTokens, final Collection tabularTokens) {
        if (CryptographicContent._logger.isLoggable(Level.FINEST)) {
            CryptographicContent._logger.finest("Tabular row has " + rowTokens.size() + " columns.");
        }
        final Iterator tokenIterator = rowTokens.iterator();
        while (tokenIterator.hasNext()) {
            tabularTokens.add(tokenIterator.next());
        }
    }
    
    private static void getFreeTextTokens(final List allTokens, final TokenRange[] tabularRanges, final Collection tokens) {
        for (int rangeIndex = 0; rangeIndex <= tabularRanges.length; ++rangeIndex) {
            final int startIndex = (rangeIndex == 0) ? 0 : tabularRanges[rangeIndex - 1].end();
            final int endIndex = (rangeIndex < tabularRanges.length) ? tabularRanges[rangeIndex].start() : allTokens.size();
            addFreeTextTokenRange(allTokens, startIndex, endIndex, tokens);
        }
    }
    
    private static TokenRange[] getTabularTokens(final Collection tokenSets, final Collection tokens) {
        if (tokenSets == null) {
            return CryptographicContent.NO_RANGES;
        }
        final TokenRange[] tabularRanges = new TokenRange[tokenSets.size()];
        if (CryptographicContent._logger.isLoggable(Level.FINER)) {
            CryptographicContent._logger.finer("Found " + tokenSets.size() + " tabular sets.");
        }
        final Iterator tokenSetIterator = tokenSets.iterator();
        int i = 0;
        while (tokenSetIterator.hasNext()) {
            tabularRanges[i] = addTabularTokenRange(tokenSetIterator.next(), tokens);
            ++i;
        }
        return tabularRanges;
    }
    
    SearchContent getSearchContent(final String cryptoKeyAlias) throws IndexException {
        SearchContent searchContent = this._cryptoKeySearchContentMap.get(cryptoKeyAlias);
        if (searchContent == null) {
            if (CryptographicContent._logger.isLoggable(Level.FINE)) {
                this._stopwatch.start();
            }
            try {
                searchContent = new SearchContent(this._tabularTextTokens, this._freeTextTokens, this._tokenizedContent, this._keyStorehouse.getKeyContainer(cryptoKeyAlias));
            }
            catch (KeyStorehouseException e) {
                throw new IndexException(IndexError.INDEX_CRYPTO_ERROR, cryptoKeyAlias, (Throwable)e);
            }
            this._cryptoKeySearchContentMap.put(cryptoKeyAlias, searchContent);
            if (CryptographicContent._logger.isLoggable(Level.FINE)) {
                CryptographicContent._logger.fine("Cryptographic hashing with key " + cryptoKeyAlias + " took " + this._stopwatch.stop().getLastTime() + " milliseconds.");
            }
        }
        return searchContent;
    }
    
    static {
        _logger = Logger.getLogger(CryptographicContent.class.getName());
        NO_RANGES = new TokenRange[0];
    }
}
