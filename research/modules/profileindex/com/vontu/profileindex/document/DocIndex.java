// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document;

import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;

public interface DocIndex
{
    public static final int[] NO_MATCHES = new int[0];
    public static final int NO_MATCH = -1;
    
    DocumentConditionViolation findPartialDocMatches(CharSequence p0, int p1) throws IndexException;
    
    DocumentConditionViolation findExactDocMatches(CharSequence p0) throws IndexException;
    
    DocumentConditionViolation findBinaryDocMatches(byte[] p0) throws IndexException;
}
