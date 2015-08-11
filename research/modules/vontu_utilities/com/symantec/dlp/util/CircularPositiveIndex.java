// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util;

public class CircularPositiveIndex
{
    private final int minIndex;
    private final int maxIndex;
    private final long totalRange;
    private int currentIndex;
    
    public CircularPositiveIndex(final int minIndex, final int maxIndex) {
        if (minIndex >= maxIndex) {
            throw new IllegalArgumentException("Minimum (" + minIndex + ") must be less than maximum (" + maxIndex + ").");
        }
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.totalRange = 1L + maxIndex - minIndex;
        this.currentIndex = minIndex;
    }
    
    public int getIndex() {
        return this.currentIndex;
    }
    
    public int getAndIncrement() {
        final int index = this.currentIndex;
        if (this.currentIndex == this.maxIndex) {
            this.currentIndex = this.minIndex;
        }
        else {
            ++this.currentIndex;
        }
        return index;
    }
    
    public int incrementAndGet() {
        this.getAndIncrement();
        return this.currentIndex;
    }
    
    public int advanceAndGet(long count) {
        if (count <= 0L) {
            throw new IllegalArgumentException("Count (" + count + ") must be positive.");
        }
        count %= this.totalRange;
        final int remainingCountToEndOfRange = this.maxIndex - this.currentIndex;
        if (count <= remainingCountToEndOfRange) {
            this.currentIndex += (int)count;
        }
        else {
            this.currentIndex = (int)(this.minIndex + (count - remainingCountToEndOfRange - 1L));
        }
        return this.currentIndex;
    }
}
