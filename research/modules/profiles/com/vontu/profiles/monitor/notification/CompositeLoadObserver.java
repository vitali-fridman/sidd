// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.notification;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Collection;

public class CompositeLoadObserver implements LoadObserver
{
    private final Collection<LoadObserver> _observers;
    
    public CompositeLoadObserver() {
        this._observers = new CopyOnWriteArraySet<LoadObserver>();
    }
    
    public void addObserver(final LoadObserver observer) {
        this._observers.add(observer);
    }
    
    public void removeObserver(final LoadObserver observer) {
        this._observers.remove(observer);
    }
    
    public void clearObservers() {
        this._observers.clear();
    }
    
    @Override
    public void loadComplete(final LoadEvent event) {
        for (final LoadObserver observer : this._observers) {
            observer.loadComplete(event);
        }
    }
    
    @Override
    public void loadFailed(final LoadFailure failure) {
        for (final LoadObserver observer : this._observers) {
            observer.loadFailed(failure);
        }
    }
}
