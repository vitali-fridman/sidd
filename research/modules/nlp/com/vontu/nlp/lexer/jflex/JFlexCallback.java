// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.jflex;

import java.util.Iterator;
import java.util.Set;
import com.vontu.util.ProtectException;
import com.vontu.nlp.lexer.Token;
import com.vontu.nlp.lexer.asiansupport.AsianTokenMergedLookup;

public class JFlexCallback
{
    private JFlexWrapper _wrapper;
    private boolean _isIndexing;
    private AsianTokenMergedLookup _asianTokenLookup;
    
    public JFlexCallback(final JFlexWrapper wrapper, final boolean isIndexing, final AsianTokenMergedLookup lookup) {
        this._wrapper = wrapper;
        this._isIndexing = isIndexing;
        this._asianTokenLookup = lookup;
    }
    
    private void createToken(final int type, final boolean validate, final int failedValidationType, final int yychar, final int yyline, final int yycolumn, final int length) throws ProtectException, InterruptedException {
        final Token token = this._wrapper.createToken(type, validate, failedValidationType, yychar, yyline, yycolumn, length);
        this._wrapper.registerToken(token);
    }
    
    public void addToken(final int type, final boolean validate, final int failedValidationType, final int yychar, final int yyline, final int yycolumn) throws ProtectException, InterruptedException {
        this.createToken(type, validate, failedValidationType, yychar, yyline, yycolumn, 0);
    }
    
    public void addToken(final int type, final int yychar, final int yyline, final int yycolumn) throws ProtectException, InterruptedException {
        this.createToken(type, false, 0, yychar, yyline, yycolumn, 0);
    }
    
    public void addAsianWordToken(final Language language, final int yychar, final int yyline, final int yycolumn, final int length, final JFlexLexer lexer) throws ProtectException, InterruptedException {
        if (this._isIndexing) {
            this.createToken(0, false, 0, yychar, yyline, yycolumn, 0);
            return;
        }
        if (this._asianTokenLookup == null) {
            return;
        }
        if (language == Language.KOREAN) {
            this.createAllTokensForList(this.checkToken(lexer.yycharat(0)), yychar, yyline, yycolumn, length);
            if (length > 1) {
                this.createAllTokensForList(this.checkToken(lexer.yycharat(1)), yychar + 1, yyline, yycolumn + 1, length - 1);
            }
        }
        else {
            for (int pos = 0, remaining = length; pos < length; ++pos, --remaining) {
                this.createAllTokensForList(this.checkToken(lexer.yycharat(pos)), yychar + pos, yyline, yycolumn + pos, remaining);
            }
        }
    }
    
    private void createAllTokensForList(final Set<Integer> lengthList, final int yychar, final int yyline, final int yycolumn, final int remainingLength) throws ProtectException, InterruptedException {
        if (lengthList == null) {
            return;
        }
        for (final Integer length : lengthList) {
            if (length <= remainingLength) {
                this.createToken(0, false, 0, yychar, yyline, yycolumn, length);
            }
        }
    }
    
    private Set<Integer> checkToken(final char checkTokens) {
        return this._asianTokenLookup.getLengths(checkTokens);
    }
    
    public enum Language
    {
        NON_WHITE_SPACE, 
        KOREAN;
    }
}
