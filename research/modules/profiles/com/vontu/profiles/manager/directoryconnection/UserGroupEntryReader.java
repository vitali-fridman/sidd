// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.directoryconnection;

import java.io.IOException;
import java.util.Iterator;
import java.io.Reader;

public class UserGroupEntryReader extends Reader
{
    private Iterator<UserGroupEntry> userGroupEntries;
    private UserGroupEntry currentUserGroupEntry;
    private int currentCharacterOffset;
    private boolean open;
    
    public UserGroupEntryReader(final Iterator<UserGroupEntry> userGroupEntries) {
        this.userGroupEntries = userGroupEntries;
        this.currentUserGroupEntry = null;
        this.currentCharacterOffset = 0;
        this.open = true;
    }
    
    @Override
    public void close() throws IOException {
        this.userGroupEntries = null;
        this.open = false;
    }
    
    @Override
    public int read(final char[] characterBuffer, int destOffset, final int totalCharactersToRead) throws IOException {
        if (!this.open) {
            throw new IOException("This reader has been closed");
        }
        if (!this.userGroupEntries.hasNext() && this.currentUserGroupEntry == null) {
            return -1;
        }
        if (this.currentUserGroupEntry == null) {
            this.currentUserGroupEntry = this.userGroupEntries.next();
        }
        int charactersRead = 0;
        while (charactersRead < totalCharactersToRead && this.currentUserGroupEntry != null) {
            final String currentUserDataRow = this.currentUserGroupEntry.getUserDataRow();
            final int charactersLeftInRow = currentUserDataRow.length() - this.currentCharacterOffset;
            if (charactersRead + charactersLeftInRow > totalCharactersToRead) {
                final int charactersLeftToRead = totalCharactersToRead - charactersRead;
                currentUserDataRow.getChars(this.currentCharacterOffset, this.currentCharacterOffset + charactersLeftToRead, characterBuffer, destOffset);
                this.currentCharacterOffset += charactersLeftToRead;
                charactersRead += charactersLeftToRead;
                destOffset += charactersLeftToRead;
            }
            else {
                currentUserDataRow.getChars(this.currentCharacterOffset, this.currentCharacterOffset + charactersLeftInRow, characterBuffer, destOffset);
                this.currentCharacterOffset += charactersLeftInRow;
                charactersRead += charactersLeftInRow;
                destOffset += charactersLeftInRow;
                if (this.currentCharacterOffset < currentUserDataRow.length()) {
                    continue;
                }
                if (this.userGroupEntries.hasNext()) {
                    this.currentUserGroupEntry = this.userGroupEntries.next();
                    this.currentCharacterOffset = 0;
                }
                else {
                    this.currentUserGroupEntry = null;
                }
            }
        }
        return charactersRead;
    }
}
