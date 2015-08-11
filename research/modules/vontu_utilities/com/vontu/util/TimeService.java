// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class TimeService
{
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public void sleep(final long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
