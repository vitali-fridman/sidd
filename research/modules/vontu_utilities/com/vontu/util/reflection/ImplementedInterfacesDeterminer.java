// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.reflection;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImplementedInterfacesDeterminer
{
    public static List<Class> getAllImplementedInterfaces(final Class source) {
        if (null == source) {
        	return Collections.emptyList();
        }
        final Class[] implementedInterfaces = source.getInterfaces();
        final List<Class> interfaces = new ArrayList<Class>();
        addNonDuplicateClasses(implementedInterfaces, interfaces);
        final List<Class> parentInterfaces = getAllImplementedInterfaces(source.getSuperclass());
        addNonDuplicateClasses(parentInterfaces, interfaces);
        return interfaces;
    }
    
    public static Class[] getAllImplementedInterfacesAsArray(final Class source) {
        return getAllImplementedInterfaces(source).toArray(new Class[0]);
    }
    
    private static void addNonDuplicateClasses(final Class[] interfacesArray, final List<Class> interfaces) {
        for (final Class c : interfacesArray) {
            if (!interfaces.contains(c)) {
                interfaces.add(c);
            }
        }
    }
    
    private static void addNonDuplicateClasses(final List<Class> implementedInterfaces, final List<Class> interfaces) {
        for (final Class c : implementedInterfaces) {
            if (!interfaces.contains(c)) {
                interfaces.add(c);
            }
        }
    }
}
