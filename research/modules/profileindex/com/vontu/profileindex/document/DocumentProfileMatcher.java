// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document;

import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;
import com.vontu.detection.condition.DocumentMatchCondition;

public interface DocumentProfileMatcher
{
    DocumentConditionViolation detectViolation(DocumentMatchCondition p0, byte[] p1) throws IndexException;
    
    DocumentConditionViolation detectViolation(DocumentMatchCondition p0, byte[] p1, CharSequence p2) throws IndexException;
}
