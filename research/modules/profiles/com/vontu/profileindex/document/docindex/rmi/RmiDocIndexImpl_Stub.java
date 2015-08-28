// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.document.docindex.rmi;

import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.document.docindex.Configuration;
import com.vontu.profileindex.IndexException;
import com.vontu.detection.output.DocumentConditionViolation;
import java.rmi.UnexpectedException;
import com.vontu.util.ProtectException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class RmiDocIndexImpl_Stub extends RemoteStub implements RmiDocIndex, Remote
{
    private static final long serialVersionUID = 2L;
    private static Method $method_dispose_0;
    private static Method $method_findBinaryDocMatches_1;
    private static Method $method_findExactDocMatches_2;
    private static Method $method_findPartialDocMatches_3;
    private static Method $method_load_4;
    static /* synthetic */ Class class$com$vontu$util$RemoteDisposable;
    static /* synthetic */ Class class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$lang$CharSequence;
    static /* synthetic */ Class class$com$vontu$profileindex$document$docindex$Configuration;
    static /* synthetic */ Class class$com$vontu$profileindex$InputStreamFactory;
    
    static {
        try {
            RmiDocIndexImpl_Stub.$method_dispose_0 = ((RmiDocIndexImpl_Stub.class$com$vontu$util$RemoteDisposable != null) ? RmiDocIndexImpl_Stub.class$com$vontu$util$RemoteDisposable : (RmiDocIndexImpl_Stub.class$com$vontu$util$RemoteDisposable = class$("com.vontu.util.RemoteDisposable"))).getMethod("dispose", (Class[])new Class[0]);
            RmiDocIndexImpl_Stub.$method_findBinaryDocMatches_1 = ((RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex = class$("com.vontu.profileindex.document.docindex.rmi.RmiDocIndex"))).getMethod("findBinaryDocMatches", (RmiDocIndexImpl_Stub.array$B != null) ? RmiDocIndexImpl_Stub.array$B : (RmiDocIndexImpl_Stub.array$B = class$("[B")));
            RmiDocIndexImpl_Stub.$method_findExactDocMatches_2 = ((RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex = class$("com.vontu.profileindex.document.docindex.rmi.RmiDocIndex"))).getMethod("findExactDocMatches", (RmiDocIndexImpl_Stub.class$java$lang$CharSequence != null) ? RmiDocIndexImpl_Stub.class$java$lang$CharSequence : (RmiDocIndexImpl_Stub.class$java$lang$CharSequence = class$("java.lang.CharSequence")));
            RmiDocIndexImpl_Stub.$method_findPartialDocMatches_3 = ((RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex = class$("com.vontu.profileindex.document.docindex.rmi.RmiDocIndex"))).getMethod("findPartialDocMatches", (RmiDocIndexImpl_Stub.class$java$lang$CharSequence != null) ? RmiDocIndexImpl_Stub.class$java$lang$CharSequence : (RmiDocIndexImpl_Stub.class$java$lang$CharSequence = class$("java.lang.CharSequence")), Integer.TYPE);
            RmiDocIndexImpl_Stub.$method_load_4 = ((RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$rmi$RmiDocIndex = class$("com.vontu.profileindex.document.docindex.rmi.RmiDocIndex"))).getMethod("load", (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$Configuration != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$Configuration : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$document$docindex$Configuration = class$("com.vontu.profileindex.document.docindex.Configuration")), (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory != null) ? RmiDocIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory : (RmiDocIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory = class$("com.vontu.profileindex.InputStreamFactory")));
        }
        catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public RmiDocIndexImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public void dispose() throws ProtectException, RemoteException {
        try {
            super.ref.invoke(this, RmiDocIndexImpl_Stub.$method_dispose_0, null, 1608810790517582623L);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (RemoteException ex2) {
            throw ex2;
        }
        catch (ProtectException ex3) {
            throw ex3;
        }
        catch (Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public DocumentConditionViolation findBinaryDocMatches(final byte[] array) throws IndexException, RemoteException {
        try {
            return (DocumentConditionViolation)super.ref.invoke(this, RmiDocIndexImpl_Stub.$method_findBinaryDocMatches_1, new Object[] { array }, 5316017584527938096L);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (RemoteException ex2) {
            throw ex2;
        }
        catch (IndexException ex3) {
            throw ex3;
        }
        catch (Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public DocumentConditionViolation findExactDocMatches(final CharSequence charSequence) throws IndexException, RemoteException {
        try {
            return (DocumentConditionViolation)super.ref.invoke(this, RmiDocIndexImpl_Stub.$method_findExactDocMatches_2, new Object[] { charSequence }, -4671703227454833225L);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (RemoteException ex2) {
            throw ex2;
        }
        catch (IndexException ex3) {
            throw ex3;
        }
        catch (Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public DocumentConditionViolation findPartialDocMatches(final CharSequence charSequence, final int n) throws IndexException, RemoteException {
        try {
            return (DocumentConditionViolation)super.ref.invoke(this, RmiDocIndexImpl_Stub.$method_findPartialDocMatches_3, new Object[] { charSequence, new Integer(n) }, 6321177953451600862L);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (RemoteException ex2) {
            throw ex2;
        }
        catch (IndexException ex3) {
            throw ex3;
        }
        catch (Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void load(final Configuration configuration, final InputStreamFactory inputStreamFactory) throws IndexException, RemoteException {
        try {
            super.ref.invoke(this, RmiDocIndexImpl_Stub.$method_load_4, new Object[] { configuration, inputStreamFactory }, 1043497817369593341L);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (RemoteException ex2) {
            throw ex2;
        }
        catch (IndexException ex3) {
            throw ex3;
        }
        catch (Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
}
