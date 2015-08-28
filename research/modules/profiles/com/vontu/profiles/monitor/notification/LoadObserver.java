// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.notification;

public interface LoadObserver
{
    void loadComplete(LoadEvent p0);
    
    void loadFailed(LoadFailure p0);
}
