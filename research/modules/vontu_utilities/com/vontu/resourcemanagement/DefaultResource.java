// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.resourcemanagement;

public abstract class DefaultResource implements Resource
{
    private final ResourceStatus status;
    
    protected DefaultResource(final ResourceStatus status) {
        this.status = status;
    }
    
    protected void assertNotReleased() throws IllegalStateException {
        if (this.status.isReleased()) {
            final String message = "The requested operation could not be completed because this resource has been released.";
            throw new IllegalStateException(message);
        }
    }
}
