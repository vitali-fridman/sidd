// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Arrays;
import com.vontu.ramindex.util.TermHash;
import com.vontu.nlp.lexer.TextToken;
import java.io.Externalizable;

public final class SearchTerm implements Externalizable
{
    private static final long serialVersionUID = 2L;
    private byte[] _value;
    private int _indexInContent;
    private int _lineInContent;
    
    public SearchTerm() {
    }
    
    SearchTerm(final byte[] cryptoHash, final int indexInContent, final int lineInContent) {
        if (cryptoHash.length != 20) {
            throw new IllegalArgumentException("The value must be 20 bytes long.");
        }
        this._value = cryptoHash;
        this._indexInContent = indexInContent;
        this._lineInContent = lineInContent;
    }
    
    public SearchTerm(final TextToken token, final byte[] cryptoHash) {
        this(cryptoHash, token.getIndex(), token.getPosition().line);
    }
    
    public byte[] getValue() {
        return this._value;
    }
    
    public int getIndexInContent() {
        return this._indexInContent;
    }
    
    public int getLineInContent() {
        return this._lineInContent;
    }
    
    @Override
    public int hashCode() {
        return TermHash.calculateHashForSearch(this._value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SearchTerm && (this == obj || Arrays.equals(this._value, ((SearchTerm)obj)._value));
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        this._indexInContent = in.readInt();
        this._lineInContent = in.readInt();
        this._value = new byte[20];
        for (int bytesRead = in.read(this._value); bytesRead < 20; bytesRead += in.read(this._value, bytesRead, this._value.length - bytesRead)) {}
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this._indexInContent);
        out.writeInt(this._lineInContent);
        out.write(this._value);
    }
}
