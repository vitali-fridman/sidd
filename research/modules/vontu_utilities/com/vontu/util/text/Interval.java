// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.text;

public class Interval implements Comparable<Interval>
{
    private int _low;
    private int _high;
    
    public Interval(int low, int high) {
        if (low > high) {
            final int temp = low;
            low = high;
            high = temp;
        }
        this._low = low;
        this._high = high;
    }
    
    public int getLow() {
        return this._low;
    }
    
    public int getHigh() {
        return this._high;
    }
    
    public int compareTo(final int i) {
        if (this._high < i) {
            return -1;
        }
        if (this._low > i) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int compareTo(final Interval o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (this._low < o._low) {
            return -1;
        }
        if (this._low > o._low) {
            return 1;
        }
        if (this._high < o._high) {
            return -1;
        }
        if (this._high > o._high) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Interval interval = (Interval)o;
        return this.compareTo(interval) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = this._low;
        result = 31 * result + this._high;
        return result;
    }
    
    public boolean contains(final int c) {
        return this._low <= c && c <= this._high;
    }
    
    boolean overlaps(final Interval b) {
        return this.contains(b._low) || this.contains(b._high) || b.contains(this._low) || b.contains(this._high);
    }
    
    public Interval joinOverlapping(final Interval b) {
        return new Interval(Math.min(this._low, b._low), Math.max(this._high, b._high));
    }
}
