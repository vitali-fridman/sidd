// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

final class CommonTermCell extends Cell
{
    public CommonTermCell(final Cell cell) {
        this(cell.row, cell.col, cell.term);
    }
    
    public CommonTermCell(final int row, final int col, final byte[] term) {
        super(row, col, term);
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.row - ((CommonTermCell)o).row;
    }
}
