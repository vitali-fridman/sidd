// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.pattern;

import com.vontu.nlp.lexer.AbstractNormalizer;
import com.vontu.nlp.lexer.Token;

final class NumberPatternMatcher implements SystemPatternMatcher
{
    @Override
    public boolean matchesSystemPattern(final Token token) {
        final int tokenType = token.getType();
        return tokenType == 6 || tokenType == 15 || ((tokenType == 1 || tokenType == 3 || tokenType == 4) && AbstractNormalizer.isOnlyDigits(token.getValue())) || (tokenType == 9 && token.getValue().length() == 5);
    }
}
