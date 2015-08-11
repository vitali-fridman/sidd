// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.DataInputStream;
import com.vontu.ramindex.util.IndexSize;

class CellFactory implements IndexEntryFactory
{
    private static final int SIZE_OF_INT = 4;
    private static final int KILO = 1024;
    
    @Override
    public int estimateMemory(final int termSize, final int spineLength, final int entryCount) {
        final long inBytes = spineLength * 4L + IndexSize.getSizeOfRowTermContent(termSize, entryCount, spineLength, 0);
        final long inKBytes = (inBytes + 512L) / 1024L;
        if (inKBytes > 2147483647L) {
            throw new IllegalArgumentException("Estimated memory is way too large: " + inKBytes + "KB.");
        }
        return (int)inKBytes;
    }
    
    @Override
    public IndexEntry read(final DataInputStream in, final int termLength) throws IOException {
        final byte[] term = new byte[termLength];
        final int row = in.readInt();
        final byte col = in.readByte();
        in.readFully(term);
        return new Cell(row, col, term);
    }
}
