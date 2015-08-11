// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.exceptionmatcher;

import com.vontu.util.config.ConfigurationException;

public class ExceptionMatcherConfigException extends ConfigurationException
{
    public ExceptionMatcherConfigException(final String message, final Throwable t) {
        super(message, t);
    }
    
    public ExceptionMatcherConfigException(final Throwable t) {
        super("Configuration Exception", t);
    }
}
