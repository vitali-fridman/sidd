// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

final class SubIndexSpec
{
    final int subIndexNumber;
    final int startRow;
    final int endRow;
    final long startIdxFilePosition;
    final int sdpCells;
    final int termLengthToRetain;
    
    SubIndexSpec(final int subIndexNumber, final int startRow, final int endRow, final long startIdxFilePosition, final int sdpCells, final int termLengthToRetain) {
        this.subIndexNumber = subIndexNumber;
        this.startRow = startRow;
        this.endRow = endRow;
        this.startIdxFilePosition = startIdxFilePosition;
        this.sdpCells = sdpCells;
        this.termLengthToRetain = termLengthToRetain;
    }
}
