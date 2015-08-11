// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.fsm;

public interface State<I, O>
{
    void enter(O p0);
    
    void processInputs(I p0, O p1, StateHolder<I, O> p2);
    
    void exit(O p0);
}
