// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.observer;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import java.util.Collection;

public abstract class Observable<Observer>
{
    private final Collection<Observer> observers;
    private final NotificationFailureHandler<Observer> failureHandler;
    
    private static <O> NotificationFailureLogger<O> createDefaultFailureHandler() {
        return new NotificationFailureLogger<O>();
    }
    
    protected abstract Notifier<Observer> getNewObserverNotifier();
    
    protected Observable() {
        this(ObserverPolicy.DUPLICATE_OBSERVERS_ALLOWED);
    }
    
    protected Observable(final ObserverPolicy policy) {
        this(policy, (NotificationFailureHandler<Observer>) createDefaultFailureHandler());
    }
    
    protected Observable(final NotificationFailureHandler<Observer> failureHandler) {
        this(ObserverPolicy.DUPLICATE_OBSERVERS_ALLOWED, failureHandler);
    }
    
    protected Observable(final ObserverPolicy policy, final NotificationFailureHandler<Observer> failureHandler) {
        this.failureHandler = failureHandler;
        this.observers = this.createObserversCollection(policy);
    }
    
    public void addObserver(final Observer observer) {
        this.observers.add(observer);
        this.getNewObserverNotifier().notify(observer);
    }
    
    public void removeObserver(final Observer observer) {
        this.observers.remove(observer);
    }
    
    protected void executeForEachObserver(final Notifier<Observer> notifier) {
        for (final Observer observer : this.observers) {
            try {
                notifier.notify(observer);
            }
            catch (Throwable e) {
                this.failureHandler.handleNotificationFailed(observer, e);
            }
        }
    }
    
    private Collection<Observer> createObserversCollection(final ObserverPolicy policy) {
        switch (policy) {
            case DUPLICATE_OBSERVERS_ALLOWED: {
                return new CopyOnWriteArrayList<Observer>();
            }
            case DUPLICATE_OBSERVERS_NOT_ALLOWED: {
                return new CopyOnWriteArraySet<Observer>();
            }
            default: {
                throw new IllegalArgumentException("Unknown observer policy type: " + policy);
            }
        }
    }
    
    protected enum ObserverPolicy
    {
        DUPLICATE_OBSERVERS_ALLOWED, 
        DUPLICATE_OBSERVERS_NOT_ALLOWED;
    }
}
