// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Collection;
import java.util.regex.Matcher;
import java.nio.CharBuffer;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class NFAPatternMatcher
{
    private String _expression;
    private Pattern _pattern;
    private List _dfaTokens;
    private List _matches;
    
    public String getExpression() {
        return this._expression;
    }
    
    public NFAPatternMatcher(final String expression) throws PatternSyntaxException {
        this._expression = expression;
        this._dfaTokens = null;
        this._matches = new ArrayList();
        if (expression != null && expression.length() > 0) {
            this._pattern = Pattern.compile(expression);
            return;
        }
        throw new PatternSyntaxException("Pattern: is illegal", expression, -1);
    }
    
    public NFAPatternMatcher(final Pattern pattern) {
        this._expression = pattern.pattern();
        this._dfaTokens = null;
        this._matches = new ArrayList();
        this._pattern = pattern;
    }
    
    public void setDfaTokens(final List dfaTokens) {
        this._dfaTokens = dfaTokens;
    }
    
    List search(final CharSequence text) {
        if (this._dfaTokens == null) {
            throw new IllegalStateException("DFA iterator has not been set");
        }
        final CharBuffer cb = CharBuffer.wrap(text);
        final Matcher m = this._pattern.matcher(cb);
        while (m.find()) {
            final NFAPatternToken occurrence = new NFAPatternToken(text, m.start(), m.end(), this._dfaTokens);
            this._matches.add(occurrence);
        }
        return this._matches;
    }
    
    public Collection getMatches() {
        return this._matches;
    }
}
