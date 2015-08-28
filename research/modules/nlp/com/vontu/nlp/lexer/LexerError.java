// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.ProtectError;

public class LexerError extends ProtectError
{
    public static final LexerError JFLEX_ERROR;
    public static final LexerError MAX_NUM_TOKENS_REACHED;
    
    protected LexerError(final int value, final String description) throws IllegalArgumentException {
        super(value, description);
    }
    
    static {
        JFLEX_ERROR = new LexerError(3001, "JFlex error");
        MAX_NUM_TOKENS_REACHED = new LexerError(3003, "Reached maximum number of tokens");
    }
}
