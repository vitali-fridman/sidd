// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.lang.annotation.Annotation;

public class AnnotatingValueCollector<T>
{
    public void collectAnnotatingValues(Class<?> targetClass, final Class<? extends Annotation> annotationClass, final Collection<T> result) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        do {
            this.addValue(targetClass, annotationClass, result);
            this.addAllInterfaces(targetClass, annotationClass, result);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null);
    }
    
    private void addAllInterfaces(final Class<?> targetClass, final Class<? extends Annotation> annotationClass, final Collection<T> result) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (final Class<?> anInterface : targetClass.getInterfaces()) {
            this.addValue(anInterface, annotationClass, result);
            this.addAllInterfaces(anInterface, annotationClass, result);
        }
    }
    
    private void addValue(final Class<?> targetClass, final Class<? extends Annotation> annotationClass, final Collection<T> result) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final Annotation annotation = targetClass.getAnnotation(annotationClass);
        if (annotation != null) {
            result.add((T)annotationClass.getMethod("value", (Class<?>[])new Class[0]).invoke(annotation, new Object[0]));
        }
    }
}
