// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

abstract class IndexHashedByTerm extends Index
{
    IndexHashedByTerm(final int targetEntryCount, final double targetColisionListLength, final int termLengthToRetain) {
        super(targetEntryCount, targetColisionListLength, termLengthToRetain);
    }
    
    @Override
    final int calculateBucket(final IndexEntry entry) {
        return Index.getFourBytesHash(entry) % this.getSpineLength();
    }
}
