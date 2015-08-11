// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.nlp.lexer.Token;

final class TermBuilder
{
    private final StringBuffer _multiTokenTermBuilder;
    private String _singleTokenTerm;
    private boolean _unexpectedSeparatorFound;
    
    TermBuilder() {
        this._multiTokenTermBuilder = new StringBuffer();
        this._singleTokenTerm = "";
    }
    
    void reset() {
        this._singleTokenTerm = "";
        this._multiTokenTermBuilder.setLength(0);
        this._unexpectedSeparatorFound = false;
    }
    
    private boolean shouldAppend(final int tokenType) {
        if (tokenType == 11) {
            return false;
        }
        if (tokenType == 12) {
            this._unexpectedSeparatorFound = true;
            return false;
        }
        return true;
    }
    
    void append(final Token token) {
        if (this.shouldAppend(token.getType())) {
            this.append(token.getBestValue());
        }
    }
    
    void append(final String bestValue) {
        if (this._singleTokenTerm.length() == 0) {
            this._singleTokenTerm = ((bestValue == null) ? "" : bestValue);
        }
        else {
            if (this._unexpectedSeparatorFound) {
                return;
            }
            if (bestValue != null) {
                if (this._multiTokenTermBuilder.length() == 0) {
                    this._multiTokenTermBuilder.append(this._singleTokenTerm);
                }
                this._multiTokenTermBuilder.append(' ').append(bestValue);
            }
        }
    }
    
    String getTerm() {
        return (this._multiTokenTermBuilder.length() > 0) ? this._multiTokenTermBuilder.toString() : this._singleTokenTerm;
    }
}
