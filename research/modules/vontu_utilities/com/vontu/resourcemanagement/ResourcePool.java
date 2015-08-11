// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import com.vontu.util.concurrent.TimeoutRuntimeException;
import java.util.concurrent.TimeUnit;

public interface ResourcePool<R extends Resource, T extends ResourceRequest<R>>
{
    R tryAcquire(T p0, long p1, TimeUnit p2) throws TimeoutRuntimeException, InterruptedException, ShutdownException;
    
    void release(R p0) throws ShutdownException;
}
