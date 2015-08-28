// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.detection.policy.engines;

import com.vontu.profileindex.ProfileIndex;

public interface ProfileIndexProvider
{
    ProfileIndex getProfileIndex(String p0);
    
    boolean isIndexLoaded(String p0);
}
