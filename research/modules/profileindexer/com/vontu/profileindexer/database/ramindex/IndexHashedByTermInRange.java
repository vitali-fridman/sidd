// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

class IndexHashedByTermInRange extends IndexHashedByTerm
{
    private final int _rangeBottom;
    private final int _rangeTop;
    
    public IndexHashedByTermInRange(final int targetEntryCount, final double targetColisionListLength, final int termLengthToRetain, final int rangeBottom, final int rangeTop) {
        super(targetEntryCount, targetColisionListLength, termLengthToRetain);
        this._rangeBottom = rangeBottom;
        this._rangeTop = rangeTop;
    }
    
    final boolean tryToAddEntry(final IndexEntry entry) {
        final int bucket = this.calculateBucket(entry);
        if (bucket >= this._rangeBottom && bucket < this._rangeTop) {
            this.addEntry(entry, bucket);
            return true;
        }
        return false;
    }
}
