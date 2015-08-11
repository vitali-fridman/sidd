// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

public class EndpointIndexWriter
{
    private static final int INT_SIZE = 4;
    private static final int HEADER_LENGTH = 8;
    private static final int FILE_BEGINING = 0;
    public static final int MAGIC_NUMBER = 62502;
    private File file;
    private RandomAccessFile indexFile;
    private int numDocs;
    
    public EndpointIndexWriter(final File file) throws IOException {
        this.numDocs = 0;
        this.file = file;
        this.indexFile = new RandomAccessFile(file, "rw");
        this.leaveSpacesForHeader();
    }
    
    public void writeEntry(final EndpointIndexEntry endpointIndexEntry) throws IOException {
        this.indexFile.writeInt(endpointIndexEntry.getDocId());
        this.indexFile.writeInt(endpointIndexEntry.getContentLength());
        this.indexFile.write(endpointIndexEntry.getMd5());
        ++this.numDocs;
    }
    
    public void writeHeader() throws IOException {
        this.indexFile.seek(0L);
        this.indexFile.writeInt(62502);
        this.indexFile.writeInt(this.numDocs);
        this.indexFile.close();
    }
    
    private void leaveSpacesForHeader() throws IOException {
        this.indexFile.write(new byte[8]);
    }
    
    public void cancelIndexWriting() throws IOException {
        this.indexFile.close();
        this.file.delete();
    }
}
