// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex;

import com.vontu.profileindex.database.SearchTerm;

public final class CandidateCell
{
    final int row;
    final byte col;
    SearchTerm searchTerm;
    
    CandidateCell(final int row, final byte col, final SearchTerm term) {
        this.row = row;
        this.col = col;
        this.searchTerm = term;
    }
    
    @Override
    public String toString() {
        return "Row:" + this.row + " Col:" + this.col;
    }
    
    @Override
    public int hashCode() {
        return this.row;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.row == ((CandidateCell)obj).row;
    }
}
