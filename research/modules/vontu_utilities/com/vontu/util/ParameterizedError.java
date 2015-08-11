// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.text.MessageFormat;

public abstract class ParameterizedError extends ProtectError
{
    protected abstract String getMessageTemplate();
    
    protected ParameterizedError(final int value) throws IllegalArgumentException {
        super(value, null);
    }
    
    private static String formatMessage(final String messageTemplate, final Object[] parameters) {
        if (parameters == null) {
            return messageTemplate;
        }
        if (messageTemplate.indexOf("{0") < 0) {
            return messageTemplate;
        }
        return MessageFormat.format(messageTemplate, parameters);
    }
    
    private String getDefaultMessage() {
        return "Protect Error " + this.getValue() + '.';
    }
    
    @Override
    public String getDescription() {
        return this.getMessage();
    }
    
    public String getDescription(final Object... parameters) {
        return formatMessage(this.getMessage(), parameters);
    }
    
    private String getMessage() {
        try {
            return this.getMessageTemplate();
        }
        catch (Exception e) {
            return this.getDefaultMessage();
        }
    }
    
    @Override
    public String toString() {
        return this.getDescription();
    }
}
