// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.annotation;

import java.util.Set;
import java.lang.annotation.Annotation;

public interface AnnotatingInstanceCreator<T>
{
    Set<T> createObjects(Class<?> p0, Class<? extends Annotation> p1);
}
