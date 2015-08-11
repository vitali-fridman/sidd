// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

class Cell extends IndexEntry
{
    final int row;
    final int col;
    
    Cell(final int row, final int col, final byte[] term) {
        super(term);
        this.row = row;
        this.col = col;
    }
    
    public static Cell read(final DataInputStream in, final int termLength) throws IOException {
        final byte[] term = new byte[termLength];
        final int row = in.readInt();
        final byte col = in.readByte();
        in.readFully(term);
        return new Cell(row, col, term);
    }
    
    @Override
    public void write(final DataOutputStream out, final int termLength) throws IOException {
        out.writeInt(this.row);
        out.write(this.col);
        out.write(this.term, 0, termLength);
    }
}
