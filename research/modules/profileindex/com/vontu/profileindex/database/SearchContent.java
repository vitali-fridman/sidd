// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.List;
import java.util.Iterator;
import com.vontu.profileindex.IndexException;
import com.vontu.keystorehouse.KeyContainer;
import java.util.Collection;
import cern.colt.map.OpenIntObjectHashMap;
import com.vontu.nlp.lexer.TokenizedContent;

final class SearchContent
{
    private final SearchTermContent _freeTextContent;
    private final SearchTermContent _tabularTextContent;
    private final TokenizedContent _tokenizedContent;
    private final OpenIntObjectHashMap _tokenIdSearchTermMap;
    
    SearchContent(final Collection tabularTextTokens, final Collection freeTextTokens, final TokenizedContent tokenizedContent, final KeyContainer hmacKey) throws IndexException {
        this._tokenIdSearchTermMap = new OpenIntObjectHashMap(tokenizedContent.getTokens().size());
        this._freeTextContent = new SearchTermContent(freeTextTokens, false, this._tokenIdSearchTermMap, hmacKey);
        this._tabularTextContent = new SearchTermContent(tabularTextTokens, true, this._tokenIdSearchTermMap, hmacKey);
        this._tokenizedContent = tokenizedContent;
    }
    
    SearchTermContent tabularTextContent() {
        return this._tabularTextContent;
    }
    
    SearchTermContent freeTextContent() {
        return this._freeTextContent;
    }
    
    Iterator getSearchTermIterator(final int[] tokenIds) {
        return new SearchTermIterator(tokenIds, this._tokenIdSearchTermMap);
    }
    
    Iterator getTokenIterator(final int[] tokenIds) {
        return new TokenIterator(tokenIds, this._tokenizedContent.getTokens());
    }
    
    private static final class TokenIterator extends TokenIndexIterator
    {
        private final List _allTokens;
        
        private TokenIterator(final int[] tokenIds, final List allTokens) {
            super(tokenIds);
            this._allTokens = allTokens;
        }
        
        @Override
        protected Object getNext(final int tokenId) {
            return this._allTokens.get(tokenId);
        }
    }
    
    private static final class SearchTermIterator extends TokenIndexIterator
    {
        private final OpenIntObjectHashMap _searchTermMap;
        
        private SearchTermIterator(final int[] tokenIds, final OpenIntObjectHashMap searchTermMap) {
            super(tokenIds);
            this._searchTermMap = searchTermMap;
        }
        
        @Override
        protected Object getNext(final int tokenId) {
            return this._searchTermMap.get(tokenId);
        }
    }
}
