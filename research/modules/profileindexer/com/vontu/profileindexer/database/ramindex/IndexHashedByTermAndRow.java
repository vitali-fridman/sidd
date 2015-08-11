// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

final class IndexHashedByTermAndRow extends Index
{
    IndexHashedByTermAndRow(final int targetEntryCount, final double targetColisionListLength, final int termLengthToRetain) {
        super(targetEntryCount, targetColisionListLength, termLengthToRetain);
    }
    
    @Override
    void addEntry(final IndexEntry entry) {
        super.addEntry(new CommonTermCell((Cell)entry));
    }
    
    @Override
    int calculateBucket(final IndexEntry cell) {
        final int bucket = (Index.getFourBytesHash(cell) % this.getSpineLength() + ((Cell)cell).row) % this.getSpineLength();
        return bucket;
    }
}
