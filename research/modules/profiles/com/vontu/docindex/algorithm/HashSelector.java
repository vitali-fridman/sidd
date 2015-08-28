// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.algorithm;

import java.util.Arrays;
import java.util.HashSet;
import com.vontu.util.CharArrayCharSequence;
import java.util.Set;

public class HashSelector
{
    private final int _k;
    private final int _t;
    private final RollingHash _rollingHash;
    private final int _w;
    private final int[] _window;
    private static final Set<HashWithPosition> EMPTY;
    
    public HashSelector(final int k, final int t) {
        if (t <= k) {
            throw new IllegalArgumentException("t must be greater then k");
        }
        this._k = k;
        this._t = t;
        this._rollingHash = new RollingHash(this._k);
        this._w = this._t - this._k + 1;
        this._window = new int[this._w];
    }
    
    public Set<HashWithPosition> getHashes(final char[] s) {
        return this.getHashes((CharSequence)new CharArrayCharSequence(s));
    }
    
    public Set<HashWithPosition> getHashes(final CharSequence c) {
        if (c.length() <= this._t) {
            return HashSelector.EMPTY;
        }
        final Set<HashWithPosition> selectedHashes = new HashSet<HashWithPosition>();
        Arrays.fill(this._window, Integer.MAX_VALUE);
        int rightEnd = 0;
        int minHashIndex = 0;
        int contentPosition = 0;
        this._rollingHash.init(c);
        contentPosition = 0;
        while (this._rollingHash.hasNext()) {
            rightEnd = (rightEnd + 1) % this._w;
            this._window[rightEnd] = this._rollingHash.next();
            if (minHashIndex == rightEnd) {
                int inWindowOffset = 0;
                for (int i = (rightEnd - 1 + this._w) % this._w, j = 1; i != rightEnd; i = (i - 1 + this._w) % this._w, ++j) {
                    if (this._window[i] < this._window[minHashIndex]) {
                        minHashIndex = i;
                        inWindowOffset = j;
                    }
                }
                if (contentPosition >= this._w) {
                    selectedHashes.add(new HashWithPosition(this._window[minHashIndex], contentPosition - inWindowOffset));
                }
            }
            else if (this._window[rightEnd] < this._window[minHashIndex]) {
                minHashIndex = rightEnd;
                if (contentPosition >= this._w) {
                    selectedHashes.add(new HashWithPosition(this._window[minHashIndex], contentPosition));
                }
            }
            ++contentPosition;
        }
        return selectedHashes;
    }
    
    static {
        EMPTY = new HashSet<HashWithPosition>(0);
    }
}
