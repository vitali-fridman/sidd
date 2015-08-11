// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.fsm;

public interface StateDescriptor<I, O>
{
    State<I, O> getInstance();
    
    String getName();
}
