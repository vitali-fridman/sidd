// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.annotation;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;

public class AnnotatingInstanceCreatorImpl<T> implements AnnotatingInstanceCreator<T>
{
    private AnnotatingValueCollector<Class<? extends T>> _collector;
    
    public AnnotatingInstanceCreatorImpl() {
        this._collector = new AnnotatingValueCollector<Class<? extends T>>();
    }
    
    @Override
    public Set<T> createObjects(final Class<?> targetClass, final Class<? extends Annotation> annotationClass) {
        try {
            final Set<Class<? extends T>> classes = new HashSet<Class<? extends T>>();
            final Set<T> result = new HashSet<T>();
            this._collector.collectAnnotatingValues(targetClass, annotationClass, classes);
            for (final Class<? extends T> aClass : classes) {
                result.add((T)aClass.newInstance());
            }
            return Collections.unmodifiableSet((Set<? extends T>)result);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        catch (IllegalAccessException e2) {
            throw new IllegalArgumentException(e2);
        }
        catch (InvocationTargetException e3) {
            throw new IllegalArgumentException(e3);
        }
        catch (InstantiationException e4) {
            throw new IllegalArgumentException(e4);
        }
    }
}
