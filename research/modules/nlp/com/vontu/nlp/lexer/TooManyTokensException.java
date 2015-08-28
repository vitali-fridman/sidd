// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.ProtectError;
import com.vontu.util.ProtectException;

final class TooManyTokensException extends ProtectException
{
    private final int _maximumNumberOfTokens;
    
    TooManyTokensException(final int maxTokens) {
        super((ProtectError)LexerError.MAX_NUM_TOKENS_REACHED);
        this._maximumNumberOfTokens = maxTokens;
    }
    
    int maximumNumberOfTokens() {
        return this._maximumNumberOfTokens;
    }
}
