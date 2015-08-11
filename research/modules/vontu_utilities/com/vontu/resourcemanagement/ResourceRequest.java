// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

import com.vontu.util.Explainable;

public interface ResourceRequest<R extends Resource> extends Explainable
{
    Object getConsumer();
}
