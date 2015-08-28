// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

public class IncrementalMembershipMapperFactory implements LdapIncrementalAttributesMapperFactory<LdapIndexSearchObject>
{
    @Override
    public LdapIncrementalAttributesMapper<LdapIndexSearchObject> createLdapIncrementalAttributesMapper() {
        return new IncrementalMembershipMapper();
    }
}
