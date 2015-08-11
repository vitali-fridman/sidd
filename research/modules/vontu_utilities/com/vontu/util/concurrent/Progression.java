// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

public class Progression
{
    private final long _scaleFactor;
    private final double _ratio;
    private final long _maxValue;
    private double _nextValue;
    
    public Progression(final long scaleFactor, final double ratio, final long maxValue) {
        this._scaleFactor = scaleFactor;
        this._ratio = ratio;
        this._maxValue = maxValue;
        this._nextValue = scaleFactor;
    }
    
    public synchronized long getNext() {
        if (this._nextValue >= this._maxValue) {
            return this._maxValue;
        }
        final double value = this._nextValue;
        this._nextValue *= this._ratio;
        return Math.round(value);
    }
    
    public synchronized void reset() {
        this._nextValue = this._scaleFactor;
    }
}
