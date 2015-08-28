// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.algorithm;

public class HashWithPosition
{
    private final int _hash;
    private final int _position;
    
    public HashWithPosition(final int hash, final int position) {
        this._hash = hash;
        this._position = position;
    }
    
    public int getHash() {
        return this._hash;
    }
    
    public int getPosition() {
        return this._position;
    }
    
    @Override
    public int hashCode() {
        return this._hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof HashWithPosition && this._hash == ((HashWithPosition)obj).getHash();
    }
    
    @Override
    public String toString() {
        return "Hash: " + this._hash + " Position: " + this._position;
    }
}
