// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import java.util.Set;

public class LdapObjectClassFilter implements LdapMembershipFilter<LdapIndexSearchObject>
{
    private String objectClass;
    
    public LdapObjectClassFilter(final String objectClass) {
        this.objectClass = objectClass;
    }
    
    @Override
    public boolean passesFilter(final LdapIndexSearchObject ldapIndexSearchObject) {
        final Set<String> searchObjectClasses = ldapIndexSearchObject.getObjectClasses();
        return searchObjectClasses.contains(this.objectClass);
    }
}
