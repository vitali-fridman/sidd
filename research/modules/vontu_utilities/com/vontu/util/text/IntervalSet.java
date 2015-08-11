// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.text;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

public class IntervalSet
{
    private Interval[] _intervals;
    
    public IntervalSet(final List<Interval> intervals) {
        this._intervals = this.normalize(intervals);
    }
    
    Interval[] normalize(final List<Interval> input) {
        final Interval[] sorted = input.toArray(new Interval[0]);
        Arrays.sort(sorted);
        if (sorted.length == 0) {
            return sorted;
        }
        final LinkedList<Interval> disjoint = new LinkedList<Interval>();
        disjoint.add(sorted[0]);
        for (int i = 1; i < sorted.length; ++i) {
            final Interval r = sorted[i];
            Interval last = disjoint.getLast();
            if (!r.overlaps(last)) {
                disjoint.add(r);
            }
            else {
                last = last.joinOverlapping(r);
                disjoint.removeLast();
                disjoint.addLast(last);
            }
        }
        return disjoint.toArray(new Interval[0]);
    }
    
    public boolean contains(final int c) {
        int low = 0;
        int high = this._intervals.length - 1;
        while (low <= high) {
            final int mid = (low + high) / 2;
            final int cmp = this._intervals[mid].compareTo(c);
            if (cmp < 0) {
                low = mid + 1;
            }
            else {
                if (cmp <= 0) {
                    return true;
                }
                high = mid - 1;
            }
        }
        return false;
    }
    
    public Iterator<Interval> getNormalizedIntervals() {
        return Arrays.asList(this._intervals).iterator();
    }
}
