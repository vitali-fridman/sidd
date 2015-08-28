// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

public class UserGroupEntry
{
    public static final char COLUMN_SEPARATOR = '|';
    public static final char LINE_SEPARATOR = '\n';
    private String email;
    private String directoryGroupId;
    
    public UserGroupEntry(final String email, final String directoryGroupId) {
        if (email == null || directoryGroupId == null) {
            throw new IllegalArgumentException("email and directoryGroupId cannot be null");
        }
        this.email = email;
        this.directoryGroupId = directoryGroupId;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getDirectoryGroupId() {
        return this.directoryGroupId;
    }
    
    public String getUserDataRow() {
        final StringBuffer userDataRow = new StringBuffer();
        userDataRow.append(this.email);
        userDataRow.append('|');
        userDataRow.append(this.directoryGroupId);
        userDataRow.append('\n');
        return userDataRow.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof UserGroupEntry)) {
            return false;
        }
        final UserGroupEntry userData = (UserGroupEntry)object;
        return this.getEmail().equals(userData.getEmail()) && this.getDirectoryGroupId().equals(userData.getDirectoryGroupId());
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.getEmail().hashCode();
        hashCode = hashCode * 37 + this.getDirectoryGroupId().hashCode();
        return hashCode;
    }
}
