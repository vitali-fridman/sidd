// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import java.util.Collection;
import com.vontu.profileindex.database.SearchTerm;
import java.io.IOException;
import com.vontu.ramindex.util.IdxRdxMetadata;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

public final class LiveIndex
{
    private static final int BUFFER_SIZE = 5242880;
    final ActIndex _actIndex;
    final UtIndex _utIndex;
    final CtIndex _ctIndex;
    
    public LiveIndex(final InputStream inputStream) throws IOException {
        final DataInputStream bufferredDataInput = new DataInputStream(new BufferedInputStream(inputStream, 5242880));
        try {
            IdxRdxMetadata.skipPastRdxHeaderMarker(bufferredDataInput);
            this._actIndex = new ActIndex(bufferredDataInput);
            this._ctIndex = new CtIndex(bufferredDataInput);
            this._utIndex = new UtIndex(bufferredDataInput);
        }
        finally {
            bufferredDataInput.close();
        }
    }
    
    public int lookupInAllCommonTerms(final int colToSearch, final SearchTerm term) {
        return this._actIndex.lookup(colToSearch, term);
    }
    
    public void lookupInUncommonTerms(final int colToSearch, final SearchTerm term, final Collection candidateCells) {
        this._utIndex.lookup(colToSearch, term, candidateCells);
    }
    
    public void lookupInCommonTerms(final int colToSearch, final SearchTerm term, final int row, final Collection cellsForRow) {
        this._ctIndex.lookup(colToSearch, term, row, cellsForRow);
    }
    
    static int compareTerms(final byte[] buffer, final int startIndex, final int length, final byte[] t2) {
        for (int toCompare = (length < t2.length) ? length : t2.length, i = 0; i < toCompare; ++i) {
            final int diff = buffer[i + startIndex] - t2[i];
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
    
    static int getInt(final byte[] bb, final int i) {
        return (bb[i] & 0xFF) << 24 | (bb[i + 1] & 0xFF) << 16 | (bb[i + 2] & 0xFF) << 8 | (bb[i + 3] & 0xFF);
    }
}
