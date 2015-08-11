// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.document;

import java.io.IOException;
import com.vontu.profileindexer.IndexerException;

public interface DocIndexCreator
{
    boolean addCharDocument(int p0, String p1, CharSequence p2) throws IndexerException, IOException;
    
    boolean replaceCharDocument(int p0, String p1, CharSequence p2, byte[] p3) throws IndexerException, IOException;
    
    void addBinaryDocument(int p0, String p1, byte[] p2) throws IndexerException, IOException;
    
    void replaceBinaryDocument(int p0, String p1, byte[] p2) throws IndexerException, IOException;
    
    void deleteDocument(int p0) throws IOException;
    
    void finish() throws IOException;
    
    void cancel() throws IOException;
    
    int getNumDocuments();
    
    long getIndexSize();
    
    boolean isRunning();
}
