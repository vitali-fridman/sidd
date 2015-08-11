// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.DataInputStream;
import com.vontu.ramindex.util.IndexSize;

final class AllCommonTermsIndexEntryFactory implements IndexEntryFactory
{
    private static final int SIZE_OF_INT = 4;
    private static final int KILO = 1024;
    
    @Override
    public int estimateMemory(final int termSize, final int spineLength, final int entryCount) {
        return (4 * spineLength + IndexSize.getSizeOfAllCommonTermContent(termSize, entryCount, spineLength, 0) + 512) / 1024;
    }
    
    @Override
    public IndexEntry read(final DataInputStream in, final int termLength) throws IOException {
        final byte[] term = new byte[termLength];
        final int colMask = in.readInt();
        in.readFully(term);
        return new AllCommonTermsIndexEntry(colMask, term);
    }
}
