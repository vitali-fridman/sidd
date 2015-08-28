// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import javax.naming.NamingEnumeration;
import java.util.logging.Level;
import java.util.HashSet;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.simple.ParameterizedContextMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.ldap.core.DistinguishedName;
import java.util.logging.Logger;

public class LdapIndexSearchObject implements MembershipProviderInterface
{
    private static final Logger logger;
    private DistinguishedName distinguishedName;
    private String email;
    private Set<String> objectClasses;
    private List<DistinguishedName> members;
    private boolean isMemberListIncomplete;
    
    public String getEmail() {
        return this.email;
    }
    
    @Override
    public List<DistinguishedName> getMembers() {
        return this.members;
    }
    
    public DistinguishedName getDistinguishedName() {
        return this.distinguishedName;
    }
    
    public Set<String> getObjectClasses() {
        return this.objectClasses;
    }
    
    public boolean isMemberListIncomplete() {
        return this.isMemberListIncomplete;
    }
    
    public LdapIndexSearchObject(final DistinguishedName distinguishedName, final String email, final List<DistinguishedName> members, final Set<String> objectClasses) {
        this(distinguishedName, email, members, objectClasses, false);
    }
    
    public LdapIndexSearchObject(final DistinguishedName distinguishedName, final String email, final List<DistinguishedName> members, final Set<String> objectClasses, final boolean isMemberListIncomplete) {
        this.distinguishedName = distinguishedName;
        this.email = email;
        this.members = members;
        this.objectClasses = objectClasses;
        this.isMemberListIncomplete = isMemberListIncomplete;
    }
    
    public LdapIndexSearchObject(final String email, final String[] memberDns) {
        if (memberDns != null) {
            this.members = new ArrayList<DistinguishedName>(memberDns.length);
            for (final String mDn : memberDns) {
                this.members.add(new DistinguishedName(mDn));
            }
        }
        else {
            this.members = new ArrayList<DistinguishedName>();
        }
        this.email = email;
    }
    
    static {
        logger = Logger.getLogger(LdapIndexSearchObject.class.getName());
    }
    
    public static class Mapper implements ParameterizedContextMapper<LdapIndexSearchObject>
    {
        public LdapIndexSearchObject mapFromContext(final Object obj) {
            final DirContextAdapter adapter = (DirContextAdapter)obj;
            final String email = adapter.getStringAttribute("mail");
            final String distinguishedName = adapter.getStringAttribute("distinguishedname");
            final String[] members = adapter.getStringAttributes("member");
            final String[] objClasses = adapter.getStringAttributes("objectclass");
            List<DistinguishedName> memberDNs;
            if (members != null) {
                memberDNs = new ArrayList<DistinguishedName>(members.length);
                for (final String member : members) {
                    memberDNs.add(new DistinguishedName(member));
                }
            }
            else {
                memberDNs = new ArrayList<DistinguishedName>();
            }
            final Set<String> objectClasses = new HashSet<String>();
            if (objClasses != null) {
                for (final String objClass : objClasses) {
                    objectClasses.add(objClass);
                }
            }
            else {
                LdapIndexSearchObject.logger.log(Level.WARNING, "Received null objectClasses");
            }
            boolean isMemberListIncomplete = false;
            final NamingEnumeration<String> attrIds = adapter.getAttributes().getIDs();
            while (attrIds.hasMoreElements()) {
                final String attrId = attrIds.nextElement();
                if (attrId.startsWith("member;")) {
                    isMemberListIncomplete = true;
                    break;
                }
            }
            final LdapIndexSearchObject ldapIndexSearchObject = new LdapIndexSearchObject(new DistinguishedName(distinguishedName), email, memberDNs, objectClasses, isMemberListIncomplete);
            return ldapIndexSearchObject;
        }
    }
}
