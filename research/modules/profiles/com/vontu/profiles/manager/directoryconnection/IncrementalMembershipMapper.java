// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.HashSet;
import org.springframework.ldap.util.AttributeValueProcessor;
import java.util.Set;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.util.IncrementalAttributeMapper;
import org.springframework.ldap.util.ListAttributeValueProcessor;

public class IncrementalMembershipMapper implements LdapIncrementalAttributesMapper<LdapIndexSearchObject>
{
    private ListAttributeValueProcessor memberAttributeValueProcessor;
    private IncrementalAttributeMapper memberAttributeMapper;
    private DistinguishedName distinguishedName;
    private String email;
    private Set<String> objectClasses;
    
    public IncrementalMembershipMapper() {
        this(new ListAttributeValueProcessor());
    }
    
    IncrementalMembershipMapper(final ListAttributeValueProcessor memberAttributeValueProcessor) {
        this(memberAttributeValueProcessor, new IncrementalAttributeMapper("member", (AttributeValueProcessor)memberAttributeValueProcessor));
    }
    
    IncrementalMembershipMapper(final ListAttributeValueProcessor memberAttributeValueProcessor, final IncrementalAttributeMapper memberAttributeMapper) {
        this.email = "";
        this.objectClasses = new HashSet<String>();
        this.memberAttributeValueProcessor = memberAttributeValueProcessor;
        this.memberAttributeMapper = memberAttributeMapper;
    }
    
    public Object mapFromAttributes(final Attributes attributes) throws NamingException {
        final Attribute email = attributes.get("mail");
        if (email != null) {
            this.email = email.get().toString();
        }
        final Attribute dn = attributes.get("distinguishedname");
        if (dn != null) {
            this.distinguishedName = new DistinguishedName(dn.get().toString());
        }
        final Attribute objectClass = attributes.get("objectclass");
        if (objectClass != null) {
            final NamingEnumeration enumeration = objectClass.getAll();
            while (enumeration.hasMore()) {
                this.objectClasses.add(enumeration.next());
            }
        }
        this.memberAttributeMapper.mapFromAttributes(attributes);
        return this;
    }
    
    @Override
    public boolean hasMore() {
        return this.memberAttributeMapper.hasMore();
    }
    
    @Override
    public String[] getAttributesArray() {
        return this.memberAttributeMapper.getAttributesArray();
    }
    
    @Override
    public LdapIndexSearchObject getMappedObject() {
        final List<String> memberDns = (List<String>)this.memberAttributeValueProcessor.getValues();
        final List<DistinguishedName> members = new ArrayList<DistinguishedName>(memberDns.size());
        for (final String memberDn : memberDns) {
            members.add(new DistinguishedName(memberDn));
        }
        final LdapIndexSearchObject ldapIndexSearchObject = new LdapIndexSearchObject(this.distinguishedName, this.email, members, this.objectClasses);
        return ldapIndexSearchObject;
    }
}
