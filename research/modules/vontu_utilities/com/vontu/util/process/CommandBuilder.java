// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collection;

public class CommandBuilder
{
    private final Collection<String> _command;
    
    public CommandBuilder() {
        this._command = new LinkedList<String>();
    }
    
    public void append(final String argument) {
        if (argument != null && argument.length() > 0) {
            this._command.add(argument);
        }
    }
    
    public void append(final String argumentName, final String argumentValue) {
        if (argumentValue != null && argumentValue.length() > 0) {
            this._command.add(argumentName + '=' + argumentValue);
        }
    }
    
    public void append(final String[] arguments) {
        this._command.addAll(Arrays.asList(arguments));
    }
    
    public void appendSystemProperty(final String propertyName) {
        this.append("-D" + propertyName, System.getProperty(propertyName));
    }
    
    public static String[] fromProperty(final String basePropertyName) {
        final Collection<String> properties = new LinkedList<String>();
        for (int i = 1; System.getProperty(basePropertyName + '.' + i) != null; ++i) {
            properties.add(System.getProperty(basePropertyName + '.' + i).trim());
        }
        return properties.toArray(new String[properties.size()]);
    }
    
    public String[] getCommand() {
        return this._command.toArray(new String[this._command.size()]);
    }
}
