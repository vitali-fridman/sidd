// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.DataOutputStream;

public abstract class IndexEntry implements Comparable
{
    final byte[] term;
    
    IndexEntry(final byte[] term) {
        this.term = term;
    }
    
    public abstract void write(final DataOutputStream p0, final int p1) throws IOException;
    
    public void write(final DataOutputStream out) throws IOException {
        this.write(out, this.term.length);
    }
    
    private int compareTo(final IndexEntry other, final int termLength) {
        final byte[] otherTerm = other.term;
        for (int i = 0; i < termLength; ++i) {
            final int diff = this.term[i] - otherTerm[i];
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.compareTo((IndexEntry)o, this.term.length);
    }
}
