// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.observer;

public interface NotificationFailureHandler<Observer>
{
    void handleNotificationFailed(Observer p0, Throwable p1);
}
