// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

public interface LdapMembershipFilter<T>
{
    boolean passesFilter(T p0);
}
