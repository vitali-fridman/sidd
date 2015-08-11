// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.annotation;

import java.util.Collections;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class CachingAnnotatingInstanceCreator<T> implements AnnotatingInstanceCreator<T>
{
    private final AnnotatingInstanceCreator<T> _creator;
    private final Map<Class<?>, Set<T>> _cache;
    
    public CachingAnnotatingInstanceCreator() {
        this((AnnotatingInstanceCreator)new AnnotatingInstanceCreatorImpl());
    }
    
    public CachingAnnotatingInstanceCreator(final AnnotatingInstanceCreator<T> creator) {
        this._cache = new HashMap<Class<?>, Set<T>>();
        this._creator = creator;
    }
    
    @Override
    public synchronized Set<T> createObjects(final Class<?> targetClass, final Class<? extends Annotation> annotationClass) {
        Set<T> result = this._cache.get(targetClass);
        if (result == null) {
            result = this._creator.createObjects(targetClass, annotationClass);
            this._cache.put(targetClass, result);
        }
        return Collections.unmodifiableSet((Set<? extends T>)result);
    }
}
