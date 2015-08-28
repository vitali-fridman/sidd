// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.ProtectException;

public abstract class TokenSetBuilder
{
    protected final int _type;
    protected boolean _done;
    protected TokenSetCollection _tokenSetCollection;
    
    protected TokenSetBuilder(final int type) {
        this._type = type;
        this._done = false;
        this._tokenSetCollection = new TokenSetCollection(type);
    }
    
    public void initialize() {
        this._done = false;
        if (this._tokenSetCollection.size() > 0) {
            this._tokenSetCollection = new TokenSetCollection(this._type);
        }
    }
    
    public void done(final TokenizedContent content) {
        this._done = true;
    }
    
    public void done() {
        this._done = true;
    }
    
    public abstract void process(final Token p0) throws ProtectException, InterruptedException;
    
    public int getType() {
        return this._type;
    }
    
    public boolean isDone() {
        return this._done;
    }
    
    public TokenSetCollection getTokenSetCollection() {
        return this._tokenSetCollection;
    }
    
    public abstract String getDescriptiveName();
}
