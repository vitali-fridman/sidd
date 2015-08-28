// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import com.vontu.ramindex.util.TermHash;
import com.vontu.profileindex.database.SearchTerm;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.vontu.ramindex.util.IndexSize;
import java.util.Arrays;
import java.io.DataInputStream;

public final class ActIndex
{
    final int _termSize;
    final int[] _spine;
    final byte[] _content;
    static final byte NOT_COL_MASK = -1;
    static final int EMPTY_BUCKET_MARKER = -1;
    
    ActIndex(final DataInputStream indexFile) throws IOException {
        this._termSize = indexFile.readInt();
        final int spineLength = indexFile.readInt();
        final int emptyBucketCount = indexFile.readInt();
        final int entryCount = indexFile.readInt();
        Arrays.fill(this._spine = new int[spineLength], -1);
        final int contentSize = IndexSize.getSizeOfAllCommonTermContent(this._termSize, entryCount, spineLength, emptyBucketCount);
        this._content = new byte[contentSize];
        final byte[] term = new byte[this._termSize];
        int contentPosition = 0;
        for (int i = 0; i < spineLength; ++i) {
            final int bucketNumberOfEntries = indexFile.readInt();
            if (bucketNumberOfEntries == 0) {
                this._spine[i] = -1;
            }
            else {
                this._spine[i] = contentPosition;
                final int bucketContentLength = bucketNumberOfEntries * (4 + this._termSize) + 1;
                final ByteBuffer bucket = ByteBuffer.wrap(this._content, contentPosition, bucketContentLength);
                for (int j = 0; j < bucketNumberOfEntries; ++j) {
                    final int colMask = indexFile.readInt();
                    indexFile.readFully(term);
                    bucket.putInt(colMask);
                    bucket.put(term);
                }
                bucket.put((byte)(-1));
                contentPosition += bucketContentLength;
            }
        }
    }
    
    int lookup(final int colToSearch, final SearchTerm term) {
        final byte[] termToLookup = term.getValue();
        final int hash = TermHash.calculateHashForSpine(termToLookup);
        final int bucketIndex = hash % this._spine.length;
        int bucketContentIndex = this._spine[bucketIndex];
        if (bucketContentIndex == -1) {
            return -1;
        }
        while (this._content[bucketContentIndex] != -1) {
            final int colMask = LiveIndex.getInt(this._content, bucketContentIndex);
            bucketContentIndex += 4;
            final int colIntersection = colMask & colToSearch;
            if (colIntersection != 0) {
                final int diff = LiveIndex.compareTerms(this._content, bucketContentIndex, this._termSize, termToLookup);
                if (diff > 0) {
                    return -1;
                }
                if (diff == 0) {
                    return colIntersection;
                }
                bucketContentIndex += this._termSize;
            }
            else {
                bucketContentIndex += this._termSize;
            }
        }
        return -1;
    }
}
