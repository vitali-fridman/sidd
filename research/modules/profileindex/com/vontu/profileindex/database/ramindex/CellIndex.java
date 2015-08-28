// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.vontu.ramindex.util.IndexSize;
import java.util.Arrays;
import java.io.DataInputStream;

public abstract class CellIndex
{
    final int _termSize;
    final int[] _spine;
    final byte[] _content;
    static final byte END_MARKER = -1;
    static final int EMPTY_BUCKET_MARKER = -1;
    
    CellIndex(final DataInputStream indexFile) throws IOException {
        this._termSize = indexFile.readInt();
        final int spineLength = indexFile.readInt();
        final int emptyBucketCount = indexFile.readInt();
        final int entryCount = indexFile.readInt();
        Arrays.fill(this._spine = new int[spineLength], -1);
        final long sizeAsLong = IndexSize.getSizeOfRowTermContent(this._termSize, entryCount, spineLength, emptyBucketCount);
        if (sizeAsLong > 2147483647L) {
            throw new IllegalArgumentException("Size of content is way too large: " + sizeAsLong);
        }
        final int contentSize = (int)sizeAsLong;
        this._content = new byte[contentSize];
        final byte[] term = new byte[this._termSize];
        int contentPosition = 0;
        int maxBucketSize = 0;
        for (int i = 0; i < spineLength; ++i) {
            final int bucketNumberOfEntries = indexFile.readInt();
            if (bucketNumberOfEntries > maxBucketSize) {
                maxBucketSize = bucketNumberOfEntries;
            }
            if (bucketNumberOfEntries == 0) {
                this._spine[i] = -1;
            }
            else {
                this._spine[i] = contentPosition;
                final int bucketContentLength = bucketNumberOfEntries * (5 + this._termSize) + 1;
                final ByteBuffer bucket = ByteBuffer.wrap(this._content, contentPosition, bucketContentLength);
                for (int j = 0; j < bucketNumberOfEntries; ++j) {
                    final int row = indexFile.readInt();
                    final byte col = indexFile.readByte();
                    indexFile.readFully(term);
                    bucket.put(col);
                    bucket.putInt(row);
                    bucket.put(term);
                }
                bucket.put((byte)(-1));
                contentPosition += bucketContentLength;
            }
        }
    }
}
