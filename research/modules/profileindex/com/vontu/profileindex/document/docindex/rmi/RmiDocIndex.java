// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.rmi;

import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.document.docindex.Configuration;
import java.rmi.RemoteException;
import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;
import com.vontu.util.RemoteDisposable;
import java.rmi.Remote;

interface RmiDocIndex extends Remote, RemoteDisposable
{
    DocumentConditionViolation findPartialDocMatches(CharSequence p0, int p1) throws IndexException, RemoteException;
    
    DocumentConditionViolation findExactDocMatches(CharSequence p0) throws IndexException, RemoteException;
    
    DocumentConditionViolation findBinaryDocMatches(byte[] p0) throws IndexException, RemoteException;
    
    void load(Configuration p0, InputStreamFactory p1) throws RemoteException, IndexException;
}
