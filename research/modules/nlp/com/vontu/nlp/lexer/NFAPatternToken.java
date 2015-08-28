// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Iterator;
import java.util.List;

public class NFAPatternToken extends Token
{
    private final int _type;
    private final TokenPosition _position;
    private final int _startTextTokenIndex;
    private final int _endtTextTokenIndex;
    private final CharSequence _charContent;
    private String[] _normalizedValues;
    private String _value;
    
    NFAPatternToken(final CharSequence charContent, final int start, final int end, final List dfaTokens) {
        this._type = 13;
        this._charContent = charContent;
        this._position = new TokenPosition(start, end - start, -1, -1);
        this._startTextTokenIndex = findClosestDFAtoken(start, dfaTokens);
        this._endtTextTokenIndex = findClosestDFAtoken(end, dfaTokens);
    }
    
    private static int findClosestDFAtoken(final int position, final List dfaTokens) {
        if (dfaTokens.size() < 1) {
            return -1;
        }
        for (final TextToken token : dfaTokens) {
            if (token.getPosition().start >= position) {
                return token.getIndex();
            }
        }
        return dfaTokens.get(dfaTokens.size() - 1).getIndex();
    }
    
    public int getStartTextTokenIndex() {
        return this._startTextTokenIndex;
    }
    
    public int getEndtTextTokenIndex() {
        return this._endtTextTokenIndex;
    }
    
    @Override
    public String[] getNormalizedValues() {
        if (this._normalizedValues == null) {
            this._normalizedValues = new String[0];
        }
        return this._normalizedValues;
    }
    
    @Override
    public String getValue() {
        if (this._value == null) {
            this._value = this._charContent.subSequence(this._position.start, this._position.start + this._position.length).toString();
        }
        return this._value;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(2048);
        sb.append("              ").append(this._startTextTokenIndex).append(":").append(this._endtTextTokenIndex).append(":").append(super.toString());
        return sb.toString();
    }
    
    @Override
    public int getType() {
        return this._type;
    }
    
    @Override
    public TokenPosition getPosition() {
        return this._position;
    }
}
