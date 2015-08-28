// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database.ramindex.rmi;

import com.vontu.profileindex.InputStreamFactory;
import com.vontu.profileindex.database.ramindex.Configuration;
import com.vontu.profileindex.database.SearchCondition;
import com.vontu.profileindex.IndexException;
import java.rmi.RemoteException;
import com.vontu.profileindex.database.DatabaseRowMatch;
import com.vontu.profileindex.database.SearchTerm;
import com.vontu.util.RemoteDisposable;
import java.rmi.Remote;

interface RmiRamIndex extends Remote, RemoteDisposable
{
    DatabaseRowMatch[] findMatches(SearchTerm[] p0, int p1, int p2, int[] p3, int p4, boolean p5) throws RemoteException, IndexException;
    
    boolean[] validateRows(SearchTerm[][] p0, int[] p1, SearchCondition p2) throws RemoteException, IndexException;
    
    void load(Configuration p0, InputStreamFactory p1) throws RemoteException, IndexException;
}
