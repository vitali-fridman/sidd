// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.ramindex.util.TermHash;
import java.util.Arrays;
import java.io.Serializable;

final class RawTerm implements Serializable
{
    private final byte[] _value;
    
    RawTerm(final byte[] value) {
        this._value = value;
    }
    
    byte[] getValue() {
        return this._value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof RawTerm && Arrays.equals(((RawTerm)obj)._value, this._value);
    }
    
    @Override
    public int hashCode() {
        return TermHash.calculateHashForSearch(this._value);
    }
}
