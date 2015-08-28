// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex;

public interface DocumentIndex
{
    int getK();
    
    int getT();
    
    int getSnippetCheckThreshold();
    
    int[] lookupHash(int p0);
    
    byte[] getSnippet(int p0, int p1);
    
    int lookupFingerprintCount(int p0);
    
    int lookupDocumentId(byte[] p0);
}
