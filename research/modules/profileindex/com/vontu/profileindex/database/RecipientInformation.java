// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

public class RecipientInformation
{
    private String email;
    private String domain;
    
    public RecipientInformation(final String email, final String domain) {
        this.email = email;
        this.domain = domain;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getDomain() {
        return this.domain;
    }
}
