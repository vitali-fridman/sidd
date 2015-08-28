// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import com.vontu.ramindex.util.TermHash;
import java.util.Collection;
import com.vontu.profileindex.database.SearchTerm;
import java.io.IOException;
import java.io.DataInputStream;

final class UtIndex extends CellIndex
{
    UtIndex(final DataInputStream indexFile) throws IOException {
        super(indexFile);
    }
    
    void lookup(final int colToSearch, final SearchTerm term, final Collection candidates) {
        final byte[] termToLookup = term.getValue();
        final int hash = TermHash.calculateHashForSpine(termToLookup);
        final int bucketIndex = hash % this._spine.length;
        int bucketContentIndex = this._spine[bucketIndex];
        if (bucketContentIndex == -1) {
            return;
        }
        while (true) {
            final byte col = this._content[bucketContentIndex];
            ++bucketContentIndex;
            if (col == -1) {
                return;
            }
            if ((colToSearch & 1 << col) != 0x0) {
                final int row = LiveIndex.getInt(this._content, bucketContentIndex);
                bucketContentIndex += 4;
                final int diff = LiveIndex.compareTerms(this._content, bucketContentIndex, this._termSize, termToLookup);
                if (diff == 0) {
                    candidates.add(new CandidateCell(row, col, term));
                }
                else if (diff > 0) {
                    return;
                }
                bucketContentIndex += this._termSize;
            }
            else {
                bucketContentIndex = bucketContentIndex + 4 + this._termSize;
            }
        }
    }
}
