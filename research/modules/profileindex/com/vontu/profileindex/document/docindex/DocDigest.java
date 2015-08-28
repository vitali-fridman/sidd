// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

import java.util.Arrays;

public class DocDigest
{
    private final byte[] _digest;
    private int _hash;
    
    public DocDigest(final byte[] digest) {
        this._digest = digest;
        this._hash = 0;
        final int numBytes = (digest.length < 4) ? digest.length : 4;
        for (int i = digest.length - numBytes; i < digest.length; ++i) {
            this._hash |= (digest[i] & 0xFF) << 8 * i;
        }
    }
    
    @Override
    public int hashCode() {
        return this._hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DocDigest && obj.hashCode() == this._hash) {
            final byte[] digest = ((DocDigest)obj).getDigest();
            return Arrays.equals(digest, this._digest);
        }
        return false;
    }
    
    private byte[] getDigest() {
        return this._digest;
    }
    
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this._digest.length; ++i) {
            result += Integer.toString((this._digest[i] & 0xFF) + 256, 16).substring(1);
        }
        return result;
    }
}
