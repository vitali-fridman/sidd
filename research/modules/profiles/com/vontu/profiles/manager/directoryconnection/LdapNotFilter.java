// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

public class LdapNotFilter implements LdapMembershipFilter<LdapIndexSearchObject>
{
    private final LdapMembershipFilter membershipFilter;
    
    public LdapNotFilter(final LdapMembershipFilter membershipFilter) {
        this.membershipFilter = membershipFilter;
    }
    
    @Override
    public boolean passesFilter(final LdapIndexSearchObject ldapIndexSearchObject) {
        return !this.membershipFilter.passesFilter(ldapIndexSearchObject);
    }
}
