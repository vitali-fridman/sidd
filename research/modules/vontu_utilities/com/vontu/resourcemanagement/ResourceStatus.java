// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

public class ResourceStatus
{
    private boolean isReleased;
    
    public ResourceStatus() {
        this.isReleased = false;
    }
    
    public void setReleased() {
        this.isReleased = true;
    }
    
    public boolean isReleased() {
        return this.isReleased;
    }
}
