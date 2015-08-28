// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import org.springframework.ldap.core.DistinguishedName;
import java.util.List;

public interface MembershipProviderInterface
{
    List<DistinguishedName> getMembers();
}
