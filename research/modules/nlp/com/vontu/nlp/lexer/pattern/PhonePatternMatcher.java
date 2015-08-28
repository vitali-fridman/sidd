// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.pattern;

import com.vontu.nlp.lexer.Token;

final class PhonePatternMatcher implements SystemPatternMatcher
{
    @Override
    public boolean matchesSystemPattern(final Token token) {
        return token.getType() == 1;
    }
}
