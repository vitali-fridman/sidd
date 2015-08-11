// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import edu.oswego.cs.dl.util.concurrent.Callable;

public interface TimedCallableFactory
{
    Callable getTimedCallable(Callable p0, long p1);
}
