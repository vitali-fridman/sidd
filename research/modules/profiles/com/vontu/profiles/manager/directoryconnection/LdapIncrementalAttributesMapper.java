// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import org.springframework.ldap.core.AttributesMapper;

public interface LdapIncrementalAttributesMapper<T> extends AttributesMapper
{
    boolean hasMore();
    
    T getMappedObject();
    
    String[] getAttributesArray();
}
