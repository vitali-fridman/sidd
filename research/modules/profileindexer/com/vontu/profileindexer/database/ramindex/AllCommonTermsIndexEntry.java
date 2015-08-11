// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

final class AllCommonTermsIndexEntry extends IndexEntry
{
    private final int _colMask;
    
    AllCommonTermsIndexEntry(final int colMask, final byte[] term) {
        super(term);
        this._colMask = colMask;
    }
    
    public static AllCommonTermsIndexEntry read(final DataInputStream in, final int termLength) throws IOException {
        final byte[] term = new byte[termLength];
        final int colMask = in.readInt();
        in.readFully(term);
        return new AllCommonTermsIndexEntry(colMask, term);
    }
    
    @Override
    public void write(final DataOutputStream out, final int termLength) throws IOException {
        out.writeInt(this._colMask);
        out.write(this.term, 0, termLength);
    }
}
