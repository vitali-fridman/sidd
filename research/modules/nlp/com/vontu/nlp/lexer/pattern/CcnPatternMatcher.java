// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.pattern;

import com.vontu.nlp.lexer.AbstractNormalizer;
import com.vontu.nlp.lexer.Token;

final class CcnPatternMatcher implements SystemPatternMatcher
{
    @Override
    public boolean matchesSystemPattern(final Token token) {
        final int tokenType = token.getType();
        return tokenType == 4 || (tokenType == 6 && AbstractNormalizer.isOnlyDigits(token.getValue()));
    }
}
