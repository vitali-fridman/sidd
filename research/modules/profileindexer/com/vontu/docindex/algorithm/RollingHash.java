// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.algorithm;

class RollingHash
{
    private static final int R = 31;
    private int _hash;
    private int _rK;
    private CharSequence _c;
    private int _k;
    private int _offset;
    
    public RollingHash(final int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive");
        }
        this._k = k;
        this._rK = 1;
        for (int i = 1; i < k; ++i) {
            this._rK *= 31;
        }
    }
    
    public void init(final CharSequence c) {
        if (this._k > c.length()) {
            throw new IllegalArgumentException("k is greater then lenght of content");
        }
        this._c = c;
        this._offset = 0;
        this._hash = 0;
        for (int i = 0; i < this._k; ++i) {
            this._hash = this._hash * 31 + this._c.charAt(i);
        }
    }
    
    public boolean hasNext() {
        return this._offset + this._k < this._c.length();
    }
    
    public int next() {
        if (this._offset + this._k >= this._c.length()) {
            throw new IllegalStateException("Past the end of content.");
        }
        final int currentHash = this._hash;
        this._hash = (this._hash - this._rK * this._c.charAt(this._offset)) * 31 + this._c.charAt(this._offset + this._k);
        ++this._offset;
        return currentHash;
    }
    
    public static void main(final String[] args) {
        final String text = "01234567890123456789012345678901234567890123456789testesteskdjfgdkfjghdkfjghdkfjghdkjgh";
        final int k = 20;
        final RollingHash rh = new RollingHash(k);
        rh.init(text);
        int i = 0;
        while (rh.hasNext()) {
            System.out.println("Hash " + i + ": " + rh.next());
            ++i;
        }
    }
}
