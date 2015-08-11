// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;

public class EndpointCurrentIndexProvider implements EndpointIndexProvider
{
    int numDocsTotal;
    int numDocs;
    DataInputStream indexInputStream;
    EndpointIndexEntry nextEntry;
    
    public EndpointCurrentIndexProvider(final DataInputStream indexInputStream) throws IOException {
        this.nextEntry = null;
        (this.indexInputStream = indexInputStream).readInt();
        this.numDocsTotal = this.indexInputStream.readInt();
        this.numDocs = 0;
        if (this.hasMoreEntries()) {
            this.nextEntry = this.readEntry();
        }
    }
    
    public EndpointCurrentIndexProvider(final File file) throws IOException {
        this(new DataInputStream(new FileInputStream(file)));
    }
    
    @Override
    public boolean hasMoreEntries() {
        final boolean hasMoreEntries = this.numDocs < this.numDocsTotal;
        if (!hasMoreEntries) {
            try {
                this.indexInputStream.close();
            }
            catch (IOException ex) {}
        }
        return hasMoreEntries;
    }
    
    @Override
    public int peekNextEntryDocId() {
        if (!this.hasMoreEntries()) {
            throw new RuntimeException("No EndpointIndexEntry exists");
        }
        return this.nextEntry.getDocId();
    }
    
    private EndpointIndexEntry readEntry() {
        try {
            final int docId = this.indexInputStream.readInt();
            final int contentLength = this.indexInputStream.readInt();
            final byte[] md5 = new byte[16];
            this.indexInputStream.readFully(md5);
            return new EndpointIndexEntry(docId, contentLength, md5);
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @Override
    public EndpointIndexEntry getNextEntry() {
        if (!this.hasMoreEntries()) {
            throw new RuntimeException("No EndpointIndexEntry exists");
        }
        ++this.numDocs;
        final EndpointIndexEntry entryToReturn = this.nextEntry;
        if (this.hasMoreEntries()) {
            this.nextEntry = this.readEntry();
        }
        return entryToReturn;
    }
    
    @Override
    public void skipNextEntry() {
        this.getNextEntry();
    }
}
