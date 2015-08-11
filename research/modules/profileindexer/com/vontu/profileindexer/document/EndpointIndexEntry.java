// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

public class EndpointIndexEntry
{
    private final int docId;
    private final int contentLength;
    private final byte[] md5;
    
    public EndpointIndexEntry(final int docId, final int contentLength, final byte[] md5) {
        this.docId = docId;
        this.contentLength = contentLength;
        this.md5 = md5;
    }
    
    public int getDocId() {
        return this.docId;
    }
    
    public int getContentLength() {
        return this.contentLength;
    }
    
    public byte[] getMd5() {
        return this.md5;
    }
}
