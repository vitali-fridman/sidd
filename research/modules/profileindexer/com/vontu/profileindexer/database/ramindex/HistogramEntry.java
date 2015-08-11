// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class HistogramEntry implements Iterable<Integer>
{
    private int _cardinality;
    private final List _bucketLocations;
    
    HistogramEntry(final int bucketLocation) {
        this._cardinality = 1;
        (this._bucketLocations = new LinkedList()).add(new Integer(bucketLocation));
    }
    
    void addLocation(final int bucketLocation) {
        ++this._cardinality;
        this._bucketLocations.add(new Integer(bucketLocation));
    }
    
    public Iterator iterator() {
        return this._bucketLocations.iterator();
    }
    
    public int getCardinality() {
        return this._cardinality;
    }
}
