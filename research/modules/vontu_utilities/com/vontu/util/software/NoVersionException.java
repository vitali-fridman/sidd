// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.software;

import com.vontu.util.config.ConfigurationException;

public class NoVersionException extends ConfigurationException
{
    public NoVersionException(final String message) {
        super(message);
    }
    
    public NoVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
