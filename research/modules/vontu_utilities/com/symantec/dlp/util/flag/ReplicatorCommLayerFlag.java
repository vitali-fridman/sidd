// 
// Decompiled by Procyon v0.5.29
// 

package com.symantec.dlp.util.flag;

public class ReplicatorCommLayerFlag
{
    private static final boolean isReplicatorCommLayerEnabled;
    
    public static boolean isEnabled() {
        return ReplicatorCommLayerFlag.isReplicatorCommLayerEnabled;
    }
    
    static {
        isReplicatorCommLayerEnabled = Boolean.valueOf(System.getProperty("com.vontu.replicatorcommlayer.enabled"));
    }
}
