// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import javax.naming.Name;
import java.util.NoSuchElementException;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import java.util.Iterator;
import java.util.HashSet;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager;
import java.util.Set;
import org.springframework.ldap.core.DistinguishedName;
import java.util.logging.Level;
import com.vontu.directory.ad.ActiveDirectoryLdapTemplateFactory;
import java.util.ArrayList;
import org.springframework.ldap.filter.PresentFilter;
import java.util.List;
import org.springframework.ldap.core.simple.ParameterizedContextMapper;
import org.springframework.ldap.filter.Filter;
import javax.naming.directory.SearchControls;
import org.springframework.ldap.core.LdapTemplate;
import java.util.logging.Logger;

public class MembershipResolver
{
    public static final int DEFAULT_PAGE_SIZE = 1000;
    private static final Logger logger;
    private final LdapTemplate ldapTemplate;
    private final SearchControls searchControls;
    private final Filter searchFilter;
    private final LdapMembershipFilter membershipFilter;
    private final ParameterizedContextMapper<LdapIndexSearchObject> contextMapper;
    private final LdapIncrementalAttributesMapperFactory<LdapIndexSearchObject> membershipMapperFactory;
    private final int pageSize;
    private List<LdapIndexSearchObject> searchResults;
    
    public MembershipResolver(final LdapMembershipFilter<LdapIndexSearchObject> membershipFilter, final ParameterizedContextMapper<LdapIndexSearchObject> contextMapper, final LdapIncrementalAttributesMapperFactory<LdapIndexSearchObject> membershipMapperFactory, final LdapTemplate ldapTemplate, final SearchControls searchControls) {
        this(membershipFilter, contextMapper, membershipMapperFactory, ldapTemplate, searchControls, 1000);
    }
    
    public MembershipResolver(final LdapMembershipFilter<LdapIndexSearchObject> membershipFilter, final ParameterizedContextMapper<LdapIndexSearchObject> contextMapper, final LdapIncrementalAttributesMapperFactory<LdapIndexSearchObject> membershipMapperFactory, final LdapTemplate ldapTemplate, final SearchControls searchControls, final int pageSize) {
        this.searchFilter = (Filter)new PresentFilter("objectclass");
        this.searchResults = new ArrayList<LdapIndexSearchObject>();
        this.membershipFilter = membershipFilter;
        this.contextMapper = contextMapper;
        this.membershipMapperFactory = membershipMapperFactory;
        this.pageSize = pageSize;
        this.ldapTemplate = ldapTemplate;
        this.searchControls = searchControls;
    }
    
    public static SearchControls createSearchControls(final int timeLimitMs) {
        final String[] returnAttributes = { "objectClass", "distinguishedName", "mail", "member" };
        final SearchControls searchControls = new SearchControls();
        searchControls.setTimeLimit(timeLimitMs);
        searchControls.setSearchScope(2);
        searchControls.setReturningAttributes(returnAttributes);
        searchControls.setReturningObjFlag(true);
        return searchControls;
    }
    
    public static LdapMembershipFilter<LdapIndexSearchObject> createMembershipFilter() {
        final LdapMembershipFilter objectClassFilter = new LdapObjectClassFilter("computer");
        return new LdapNotFilter(objectClassFilter);
    }
    
    public static LdapTemplate createLdapTemplate(final String host, final int port, final boolean useSSL, final String username, final String password) {
        final ActiveDirectoryLdapTemplateFactory factory = new ActiveDirectoryLdapTemplateFactory();
        try {
            final LdapTemplate ldapTemplate = factory.createTransactionAwareLdapTemplate(host, port, useSSL, username, password);
            return ldapTemplate;
        }
        catch (Exception e) {
            MembershipResolver.logger.log(Level.SEVERE, "Ldap Source initialization failed", e);
            throw new RuntimeException(e);
        }
    }
    
    public Set<LdapIndexSearchObject> getAllChildren(final DistinguishedName searchDN) {
        final ContextSourceTransactionManager tm = new ContextSourceTransactionManager();
        tm.setContextSource(this.ldapTemplate.getContextSource());
        final TransactionTemplate tx = new TransactionTemplate((PlatformTransactionManager)tm);
        final Set<LdapIndexSearchObject> results = (Set<LdapIndexSearchObject>)tx.execute((TransactionCallback)new TransactionCallback<Set<LdapIndexSearchObject>>() {
            public Set<LdapIndexSearchObject> doInTransaction(final TransactionStatus arg0) {
                final Set<LdapIndexSearchObject> user_results = new HashSet<LdapIndexSearchObject>();
                final Iterator<LdapIndexSearchObject> it = new MembershipIterator(searchDN);
                while (it.hasNext()) {
                    final LdapIndexSearchObject searchObject = it.next();
                    user_results.add(searchObject);
                }
                return user_results;
            }
        });
        return results;
    }
    
    static {
        logger = Logger.getLogger(MembershipResolver.class.getName());
    }
    
    private class MembershipIterator implements Iterator<LdapIndexSearchObject>
    {
        private PagedResultsDirContextProcessor pageTracker;
        private boolean searchComplete;
        private final DistinguishedName searchDN;
        private Set<DistinguishedName> iteratedGroupNames;
        
        public MembershipIterator(final DistinguishedName searchDN) {
            this.pageTracker = new PagedResultsDirContextProcessor(MembershipResolver.this.pageSize);
            this.searchComplete = false;
            this.iteratedGroupNames = new HashSet<DistinguishedName>();
            this.searchDN = searchDN;
        }
        
        @Override
        public boolean hasNext() {
            if (MembershipResolver.this.searchResults.size() == 0 && !this.searchComplete) {
                this.search();
            }
            return MembershipResolver.this.searchResults.size() > 0;
        }
        
        @Override
        public LdapIndexSearchObject next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final LdapIndexSearchObject item = MembershipResolver.this.searchResults.remove(0);
            if (item.getMembers().isEmpty()) {
                return item;
            }
            this.iteratedGroupNames.add(item.getDistinguishedName());
            for (final DistinguishedName dn : item.getMembers()) {
                if (!this.iteratedGroupNames.contains(dn)) {
                    final LdapIndexSearchObject indexSearchObject = this.lookUpLdapObject(dn);
                    this.updateSearchResults(indexSearchObject);
                }
            }
            return item;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private void search() {
            final List<LdapIndexSearchObject> indexSearchObjects = (List<LdapIndexSearchObject>)MembershipResolver.this.ldapTemplate.search((Name)this.searchDN, MembershipResolver.this.searchFilter.encode(), MembershipResolver.this.searchControls, (ContextMapper)MembershipResolver.this.contextMapper, (DirContextProcessor)this.pageTracker);
            for (LdapIndexSearchObject indexSearchObject : indexSearchObjects) {
                if (indexSearchObject.isMemberListIncomplete()) {
                    indexSearchObject = this.lookUpLdapObject(indexSearchObject.getDistinguishedName());
                }
                this.updateSearchResults(indexSearchObject);
            }
            if (this.pageTracker.getCookie().getCookie() == null) {
                this.searchComplete = true;
            }
        }
        
        private void updateSearchResults(final LdapIndexSearchObject indexSearchObject) {
            if (MembershipResolver.this.membershipFilter.passesFilter(indexSearchObject)) {
                MembershipResolver.this.searchResults.add(indexSearchObject);
            }
        }
        
        private LdapIndexSearchObject lookUpLdapObject(final DistinguishedName dn) {
            final LdapIncrementalAttributesMapper<LdapIndexSearchObject> membershipMapper = MembershipResolver.this.membershipMapperFactory.createLdapIncrementalAttributesMapper();
            MembershipResolver.this.ldapTemplate.lookup((Name)dn, (AttributesMapper)membershipMapper);
            while (membershipMapper.hasMore()) {
                MembershipResolver.this.ldapTemplate.lookup((Name)dn, membershipMapper.getAttributesArray(), (AttributesMapper)membershipMapper);
            }
            final LdapIndexSearchObject ldapObject = membershipMapper.getMappedObject();
            return ldapObject;
        }
    }
}
