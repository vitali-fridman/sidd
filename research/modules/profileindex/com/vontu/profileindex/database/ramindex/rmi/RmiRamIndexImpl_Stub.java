// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.rmi;

import com.vontu.profileindex.database.SearchCondition;
import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.database.ramindex.Configuration;
import com.vontu.profileindex.IndexException;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchTerm;
import java.rmi.UnexpectedException;
import com.vontu.util.ProtectException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class RmiRamIndexImpl_Stub extends RemoteStub implements RmiRamIndex, Remote
{
    private static final long serialVersionUID = 2L;
    private static Method $method_dispose_0;
    private static Method $method_findMatches_1;
    private static Method $method_load_2;
    private static Method $method_validateRows_3;
    static /* synthetic */ Class class$com$vontu$util$RemoteDisposable;
    static /* synthetic */ Class class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex;
    static /* synthetic */ Class array$Lcom$vontu$profileindex$database$SearchTerm;
    static /* synthetic */ Class array$I;
    static /* synthetic */ Class class$com$vontu$profileindex$database$ramindex$Configuration;
    static /* synthetic */ Class class$com$vontu$profileindex$InputStreamFactory;
    static /* synthetic */ Class array$$Lcom$vontu$profileindex$database$SearchTerm;
    static /* synthetic */ Class class$com$vontu$profileindex$database$SearchCondition;
    
    static {
        try {
            RmiRamIndexImpl_Stub.$method_dispose_0 = ((RmiRamIndexImpl_Stub.class$com$vontu$util$RemoteDisposable != null) ? RmiRamIndexImpl_Stub.class$com$vontu$util$RemoteDisposable : (RmiRamIndexImpl_Stub.class$com$vontu$util$RemoteDisposable = class$("com.vontu.util.RemoteDisposable"))).getMethod("dispose", (Class[])new Class[0]);
            RmiRamIndexImpl_Stub.$method_findMatches_1 = ((RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex = class$("com.vontu.profileindex.database.ramindex.rmi.RmiRamIndex"))).getMethod("findMatches", (RmiRamIndexImpl_Stub.array$Lcom$vontu$profileindex$database$SearchTerm != null) ? RmiRamIndexImpl_Stub.array$Lcom$vontu$profileindex$database$SearchTerm : (RmiRamIndexImpl_Stub.array$Lcom$vontu$profileindex$database$SearchTerm = class$("[Lcom.vontu.profileindex.database.SearchTerm;")), Integer.TYPE, Integer.TYPE, (RmiRamIndexImpl_Stub.array$I != null) ? RmiRamIndexImpl_Stub.array$I : (RmiRamIndexImpl_Stub.array$I = class$("[I")), Integer.TYPE, Boolean.TYPE);
            RmiRamIndexImpl_Stub.$method_load_2 = ((RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex = class$("com.vontu.profileindex.database.ramindex.rmi.RmiRamIndex"))).getMethod("load", (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$Configuration != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$Configuration : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$Configuration = class$("com.vontu.profileindex.database.ramindex.Configuration")), (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$InputStreamFactory = class$("com.vontu.profileindex.InputStreamFactory")));
            RmiRamIndexImpl_Stub.$method_validateRows_3 = ((RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$ramindex$rmi$RmiRamIndex = class$("com.vontu.profileindex.database.ramindex.rmi.RmiRamIndex"))).getMethod("validateRows", (RmiRamIndexImpl_Stub.array$$Lcom$vontu$profileindex$database$SearchTerm != null) ? RmiRamIndexImpl_Stub.array$$Lcom$vontu$profileindex$database$SearchTerm : (RmiRamIndexImpl_Stub.array$$Lcom$vontu$profileindex$database$SearchTerm = class$("[[Lcom.vontu.profileindex.database.SearchTerm;")), (RmiRamIndexImpl_Stub.array$I != null) ? RmiRamIndexImpl_Stub.array$I : (RmiRamIndexImpl_Stub.array$I = class$("[I")), (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$SearchCondition != null) ? RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$SearchCondition : (RmiRamIndexImpl_Stub.class$com$vontu$profileindex$database$SearchCondition = class$("com.vontu.profileindex.database.SearchCondition")));
        }
        catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public RmiRamIndexImpl_Stub(final RemoteRef remoteRef) {
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
            super.ref.invoke(this, RmiRamIndexImpl_Stub.$method_dispose_0, null, 1608810790517582623L);
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
    
    public DatabaseRowMatch[] findMatches(final SearchTerm[] array, final int n, final int n2, final int[] array2, final int n3, final boolean b) throws IndexException, RemoteException {
        try {
            return (DatabaseRowMatch[])super.ref.invoke(this, RmiRamIndexImpl_Stub.$method_findMatches_1, new Object[] { array, new Integer(n), new Integer(n2), array2, new Integer(n3), b ? Boolean.TRUE : Boolean.FALSE }, -4922556777892452662L);
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
            super.ref.invoke(this, RmiRamIndexImpl_Stub.$method_load_2, new Object[] { configuration, inputStreamFactory }, 1470097790273923717L);
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
    
    public boolean[] validateRows(final SearchTerm[][] array, final int[] array2, final SearchCondition searchCondition) throws IndexException, RemoteException {
        try {
            return (boolean[])super.ref.invoke(this, RmiRamIndexImpl_Stub.$method_validateRows_3, new Object[] { array, array2, searchCondition }, -1980440083909466245L);
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
