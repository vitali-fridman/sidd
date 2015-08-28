// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.Iterator;
import java.util.ArrayList;
import com.vontu.security.crypto.CryptoException;
import com.vontu.profileindex.IndexError;
import com.vontu.nlp.lexer.TextToken;
import com.vontu.profileindex.IndexException;
import com.vontu.keystorehouse.KeyContainer;
import cern.colt.map.OpenIntObjectHashMap;
import java.util.Collection;

final class SearchTermContent
{
    private final SearchTerm[] _searchTerms;
    private final boolean _isTablular;
    
    SearchTermContent(final Collection tokens, final boolean isTabular, final OpenIntObjectHashMap tokenIdSearchTermMap, final KeyContainer hmacKey) throws IndexException {
        this._searchTerms = createSearchTerms(tokens, hmacKey, tokenIdSearchTermMap);
        this._isTablular = isTabular;
    }
    
    public static SearchTerm createSearchTerm(final TextToken token, final String bestValue, final KeyContainer hmacKey) throws IndexException {
        try {
            return new SearchTerm(token, hmacKey.computeDigest(bestValue));
        }
        catch (CryptoException e) {
            throw new IndexException(IndexError.INDEX_CRYPTO_ERROR, hmacKey.getAlias(), (Throwable)e);
        }
    }
    
    private static SearchTerm[] createSearchTerms(final Collection tokens, final KeyContainer hmacKey, final OpenIntObjectHashMap tokenIdSearchTermMap) throws IndexException {
        final Collection searchTerms = new ArrayList(tokens.size());
        for (final TextToken token : tokens) {
            if (token.getType() == 11) {
                continue;
            }
            if (token.getType() == 12) {
                continue;
            }
            final String bestValue = token.getBestValue();
            if (bestValue == null) {
                continue;
            }
            final SearchTerm searchTerm = createSearchTerm(token, bestValue, hmacKey);
            searchTerms.add(searchTerm);
            tokenIdSearchTermMap.put(token.getIndex(), (Object)searchTerm);
        }
        return searchTerms.toArray(new SearchTerm[searchTerms.size()]);
    }
    
    public boolean isTabular() {
        return this._isTablular;
    }
    
    public SearchTerm[] searchTerms() {
        return this._searchTerms;
    }
}
