// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import org.springframework.util.StringUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import org.springframework.ldap.core.DistinguishedName;
import com.vontu.model.data.DirectoryGroupEntry;
import java.util.HashSet;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.simple.ParameterizedContextMapper;
import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.enforce.spring.common.ServiceLocator;
import com.vontu.enforce.directory.domain.DirectoryConnectionService;
import com.vontu.model.data.DirectoryConnection;

public class UserGroupEntryReaderCreator
{
    private DirectoryConnection directoryConnection;
    private MembershipResolver membershipResolver;
    
    UserGroupEntryReaderCreator(final DirectoryConnection directoryConnection, final MembershipResolver membershipResolver) {
        this.membershipResolver = membershipResolver;
        this.directoryConnection = directoryConnection;
    }
    
    public UserGroupEntryReaderCreator(final DirectoryConnection directoryConnection) {
        this(directoryConnection, getMembershipResolver(directoryConnection));
    }
    
    private static MembershipResolver getMembershipResolver(final DirectoryConnection directoryConnection) {
        boolean useSSL = false;
        if (directoryConnection.getUseSSL() == 1) {
            useSSL = true;
        }
        final String password = ((DirectoryConnectionService)ServiceLocator.getService((Class)DirectoryConnectionService.class)).getDirectoryConnectionPassword(directoryConnection);
        final LdapTemplate ldapTemplate = MembershipResolver.createLdapTemplate(directoryConnection.getHost(), directoryConnection.getPort(), useSSL, directoryConnection.getUsername(), password);
        final MembershipResolver membershipResolver = new MembershipResolver(MembershipResolver.createMembershipFilter(), (ParameterizedContextMapper<LdapIndexSearchObject>)new LdapIndexSearchObject.Mapper(), new IncrementalMembershipMapperFactory(), ldapTemplate, MembershipResolver.createSearchControls(IndexerConfig.getDirectoryBrowserTimeoutMs()));
        return membershipResolver;
    }
    
    public UserGroupEntryReader createUserGroupEntryReader() {
        final Set<UserGroupEntry> userGroupEntryResults = new HashSet<UserGroupEntry>();
        final Iterator<DirectoryGroupEntry> directoryGroupEntries = (Iterator<DirectoryGroupEntry>)this.directoryConnection.directoryGroupEntryIterator();
        while (directoryGroupEntries.hasNext()) {
            final DirectoryGroupEntry directoryGroupEntry = directoryGroupEntries.next();
            final int directoryGroupId = directoryGroupEntry.getDirectoryGroup().getDirectoryGroupID();
            userGroupEntryResults.addAll(this.getUniqueUserGroupEntries(Integer.toString(directoryGroupId), new DistinguishedName(directoryGroupEntry.getDistinguishedName())));
        }
        return new UserGroupEntryReader(userGroupEntryResults.iterator());
    }
    
    private Set<UserGroupEntry> getUniqueUserGroupEntries(final String directoryGroupId, final DistinguishedName distinguishedName) {
        final Set<LdapIndexSearchObject> results = this.membershipResolver.getAllChildren(distinguishedName);
        final Set<UserGroupEntry> user_results = new HashSet<UserGroupEntry>();
        for (final LdapIndexSearchObject searchObject : results) {
            if (StringUtils.hasText(searchObject.getEmail())) {
                user_results.add(new UserGroupEntry(searchObject.getEmail(), directoryGroupId));
            }
        }
        return user_results;
    }
}
