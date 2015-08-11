// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

import com.vontu.util.ProtectError;
import com.vontu.util.ProtectRuntimeException;

public class ConfigurationException extends ProtectRuntimeException
{
    public ConfigurationException(final String message) {
        super(ProtectError.CONFIG_ERROR, message);
    }
    
    public ConfigurationException(final String message, final Throwable cause) {
        super(ProtectError.CONFIG_ERROR, cause, message);
    }
}
