// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.nlp.lexer.TextToken;
import java.util.ArrayList;
import java.util.Collections;
import com.vontu.detection.condition.DatabaseMatchPredicate;
import com.vontu.profileindex.IndexException;
import java.util.Collection;
import java.util.Iterator;
import com.vontu.keystorehouse.KeyContainer;
import com.vontu.detection.condition.DatabaseMatchCondition;
import com.vontu.profileindex.tokenizer.TokenizedContentFactory;

final class ConditionPredicate
{
    private final SearchTerm[] _operandSearchTerms;
    private final SearchCondition _conditionWithPredicate;
    private final TokenizedContentFactory tokenFactory;
    private static final String _dummyString = "1111111111111111111111112";
    
    ConditionPredicate(final DatabaseMatchCondition matchCondition, final KeyContainer hmacKey, final int matchedTokensCount) throws IndexException {
        this.tokenFactory = new PredicateOperandTokenFactory();
        final Collection operandTokens = this.getOperandTokens(matchCondition.predicate());
        this._operandSearchTerms = createOperandSearchTerms(operandTokens.iterator(), hmacKey);
        this._conditionWithPredicate = ((this._operandSearchTerms.length > 0) ? new DatabaseSearchConditionWithPredicate(matchCondition, matchedTokensCount) : null);
    }
    
    private Collection getOperandTokens(final DatabaseMatchPredicate predicate) {
        if (predicate == null) {
            return Collections.EMPTY_LIST;
        }
        final String operands = predicate.operands();
        if (operands == null || operands.trim().length() == 0) {
            return Collections.EMPTY_LIST;
        }
        return this.tokenFactory.newInstance(makeTabular(operands)).getTokens();
    }
    
    static SearchTerm[] createOperandSearchTerms(final Iterator operandTokenIterator, final KeyContainer hmacKey) throws IndexException {
        final Collection operandSearchTerms = new ArrayList();
        while (operandTokenIterator.hasNext()) {
            final TextToken token = operandTokenIterator.next();
            final String value = token.getValue();
            if (value.contains("1111111111111111111111112")) {
                break;
            }
            final String bestValue = token.getBestValue();
            if (bestValue == null) {
                continue;
            }
            operandSearchTerms.add(SearchTermContent.createSearchTerm(token, bestValue, hmacKey));
        }
        return operandSearchTerms.toArray(new SearchTerm[operandSearchTerms.size()]);
    }
    
    SearchCondition getSearchCondition() {
        return this._conditionWithPredicate;
    }
    
    boolean isAvailable() {
        return this._operandSearchTerms.length > 0;
    }
    
    SearchTerm[] createSeachTerms(final DatabaseRowMatch match, final SearchContent searchContent) {
        final SearchTerm[] searchTerms = new SearchTerm[this._operandSearchTerms.length + match.getTokenIds().length];
        System.arraycopy(this._operandSearchTerms, 0, searchTerms, 0, this._operandSearchTerms.length);
        setMatchedSearchTerms(searchContent.getSearchTermIterator(match.getTokenIds()), this._operandSearchTerms.length, searchTerms);
        return searchTerms;
    }
    
    static String makeTabular(String operands) {
        operands += "|1111111111111111111111112";
        for (int dummyTerms = 3 - operands.split("|").length, i = 0; i < dummyTerms; ++i) {
            operands += "|1111111111111111111111112";
        }
        return operands + "\r\n" + operands;
    }
    
    Collection tokenize(final String input) {
        if (input != null && input.trim().length() != 0) {
            return this.tokenFactory.newInstance(makeTabular(input)).getTokens();
        }
        return Collections.EMPTY_LIST;
    }
    
    static ConditionPredicate createConditionPredicate(final DatabaseMatchCondition matchCondition, final int matchedTokens, final KeyContainer keyContainer) throws IndexException {
        return new ConditionPredicate(matchCondition, keyContainer, matchedTokens);
    }
    
    static void setMatchedSearchTerms(final Iterator matchedTermIterator, final int initialIndex, final SearchTerm[] predicateSearchTerms) {
        int i = initialIndex;
        while (matchedTermIterator.hasNext()) {
            final SearchTerm matchedTerm = matchedTermIterator.next();
            predicateSearchTerms[i] = new SearchTerm(matchedTerm.getValue(), i, 0);
            ++i;
        }
    }
}
