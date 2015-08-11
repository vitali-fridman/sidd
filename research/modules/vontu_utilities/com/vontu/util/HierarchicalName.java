// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class HierarchicalName
{
    private static final String WILDCARD = "*";
    private String _name;
    private String[] _hierarchy;
    
    public HierarchicalName(final String name) {
        this._hierarchy = null;
        this._name = name;
        this._hierarchy = this._name.split("\\.");
    }
    
    public String getName() {
        return this._name;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof HierarchicalName) {
            final HierarchicalName name = (HierarchicalName)o;
            return this._name.equals(name._name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this._name.hashCode();
    }
    
    @Override
    public String toString() {
        return this._name;
    }
    
    public String[] getNameHierachy() {
        return this._hierarchy;
    }
    
    public boolean implies(final HierarchicalName name) {
        if (this.equals(name)) {
            return true;
        }
        final String[] impliesHierarchy = name.getNameHierachy();
        final String[] hierarchy = this.getNameHierachy();
        if (hierarchy.length <= impliesHierarchy.length) {
            final String lastName = hierarchy[hierarchy.length - 1];
            if (lastName.equals(impliesHierarchy[hierarchy.length - 1]) || lastName.equals("*")) {
                for (int i = 0; i < hierarchy.length - 1; ++i) {
                    if (!hierarchy[i].equals(impliesHierarchy[i]) && !hierarchy[i].equals("*")) {
                        return false;
                    }
                }
                return hierarchy.length == impliesHierarchy.length || lastName.equals("*");
            }
        }
        return false;
    }
}
