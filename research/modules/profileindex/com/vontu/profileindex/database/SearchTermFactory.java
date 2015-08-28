// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import com.vontu.profileindex.IndexException;
import java.util.Collection;
import java.util.Iterator;
import com.vontu.keystorehouse.KeyContainer;

class SearchTermFactory
{
    private final KeyContainer keyContainer;
    
    SearchTermFactory(final KeyContainer keyContainer) {
        this.keyContainer = keyContainer;
    }
    
    protected SearchTerm[] createOperandSearchTerms(final String values) throws IndexException {
        final String tabularOperand = ConditionPredicate.makeTabular(values);
        final Collection tokens = new PredicateOperandTokenFactory().newInstance(tabularOperand).getTokens();
        return ConditionPredicate.createOperandSearchTerms(tokens.iterator(), this.keyContainer);
    }
}
