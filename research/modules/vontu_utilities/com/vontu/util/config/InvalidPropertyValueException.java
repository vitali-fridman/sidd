// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.config;

public class InvalidPropertyValueException extends ConfigurationException
{
    private static final long serialVersionUID = 1L;
    private final String propertyName;
    private final String propertyValue;
    
    public InvalidPropertyValueException(final String message, final String propertyName, final String propertyValue) {
        super(message);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }
    
    public InvalidPropertyValueException(final String message, final Throwable cause, final String propertyName, final String propertyValue) {
        super(message, cause);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public String getPropertyValue() {
        return this.propertyValue;
    }
}
