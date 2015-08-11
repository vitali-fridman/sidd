// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;


public class Latch
{
    private final edu.oswego.cs.dl.util.concurrent.Latch _latch;
    
    public Latch() {
        this._latch = new edu.oswego.cs.dl.util.concurrent.Latch();
    }
    
    public void acquire() throws InterruptedException {
        this._latch.acquire();
    }
    
    public boolean attempt(final long msecs) throws InterruptedException {
        return this._latch.attempt(msecs);
    }
    
    public void release() {
        this._latch.release();
    }
}
