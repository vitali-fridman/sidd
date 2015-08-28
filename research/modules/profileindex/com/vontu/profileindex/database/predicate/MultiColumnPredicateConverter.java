// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.predicate;

import java.util.Iterator;
import java.util.ArrayList;
import com.vontu.profileindex.database.RowMatcher;
import com.vontu.detection.condition.MultiColumnPredicate;
import java.util.Collection;

class MultiColumnPredicateConverter
{
    public Collection<RowMatcher> createMatchers(final Collection<MultiColumnPredicate> predicates) {
        final Collection<RowMatcher> matchers = new ArrayList<RowMatcher>(predicates.size());
        for (final MultiColumnPredicate predicate : predicates) {
            final int[] columns = predicate.columns();
            final String values = predicate.valuesCsv();
            if (columns.length == 0) {
                throw new IllegalArgumentException("Invalid predicate; no columns defined");
            }
            if (values == null || values.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid predicate: no values defined");
            }
            matchers.add(new RowMatcherImpl(columns, values));
        }
        return matchers;
    }
}
