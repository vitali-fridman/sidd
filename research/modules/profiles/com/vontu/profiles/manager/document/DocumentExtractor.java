// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.cracker.EncodedString;
import com.vontu.cracker.DocumentType;
import com.vontu.cracker.MessageTimeOut;

public interface DocumentExtractor
{
    EncodedString getTextForFile(byte[] p0, String p1, String p2, MessageTimeOut p3, DocumentType p4) throws DocumentExtractionException;
    
    DocumentType getTypeForFile(byte[] p0, MessageTimeOut p1) throws DocumentExtractionException;
}
