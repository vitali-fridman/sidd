// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.parse;

public class CommandLineParameter
{
    private String _parameter;
    private boolean _required;
    private String _defaultValue;
    private String _description;
    
    public CommandLineParameter(final String parameter, final boolean required, final String description, final String defaultValue) {
        this._parameter = parameter;
        this._required = required;
        this._defaultValue = defaultValue;
        this._description = description;
    }
    
    public CommandLineParameter(final String parameter, final boolean required, final String description) {
        this(parameter, required, description, null);
    }
    
    public String getParameter() {
        return this._parameter;
    }
    
    public boolean isRequired() {
        return this._required;
    }
    
    public String getDefaultValue() {
        return this._defaultValue;
    }
    
    public String getDescription() {
        return this._description;
    }
    
    @Override
    public int hashCode() {
        return this._parameter.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof CommandLineParameter && this._parameter.equals(((CommandLineParameter)o).getParameter());
    }
}
