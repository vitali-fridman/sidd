// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.parse;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

public class CommandLineParser
{
    HashSet<CommandLineParameter> _availableArgs;
    Map<String, String> _parseResults;
    List<String> _nonParameters;
    
    public CommandLineParser() {
        this._availableArgs = new HashSet<CommandLineParameter>();
        this._parseResults = new HashMap<String, String>();
        this._nonParameters = new ArrayList<String>();
    }
    
    public void addParameter(final CommandLineParameter parameter) {
        this._availableArgs.add(parameter);
    }
    
    public void parse(final String[] args) throws CommandLineException {
        for (final String s : args) {
            if (s.startsWith("-")) {
                if (s.length() == 1) {
                    throw new CommandLineException("malformed argument \"-\"");
                }
                final String parameter = this.parseParameter(s);
                final String value = this.parseValue(s);
                this._parseResults.put(parameter, value);
            }
            else {
                this._nonParameters.add(s);
            }
        }
        this.scrub();
    }
    
    public boolean isParameterSet(final String parameter) {
        return this._parseResults.containsKey(parameter);
    }
    
    public String getValue(final String parameter) {
        return this._parseResults.get(parameter);
    }
    
    public List<String> getNonParameterArguments() {
        return Collections.unmodifiableList((List<? extends String>)this._nonParameters);
    }
    
    public CommandLineParameter[] getKnownParameters() {
        final CommandLineParameter[] parameters = this._availableArgs.toArray(new CommandLineParameter[this._availableArgs.size()]);
        Arrays.sort(parameters, new Comparator<CommandLineParameter>() {
            @Override
            public int compare(final CommandLineParameter a, final CommandLineParameter b) {
                return a.getParameter().compareToIgnoreCase(b.getParameter());
            }
        });
        return parameters;
    }
    
    private void scrub() throws CommandLineException {
        for (final CommandLineParameter parameter : this._availableArgs) {
            final String parameterName = parameter.getParameter();
            if (this.getValue(parameterName) == null) {
                if (parameter.isRequired()) {
                    throw new CommandLineException("Missing value for parameter " + parameterName);
                }
                final String defaultValue = parameter.getDefaultValue();
                if (defaultValue == null) {
                    continue;
                }
                this._parseResults.put(parameterName, defaultValue);
            }
        }
    }
    
    private String parseParameter(final String s) {
        final int index = s.indexOf(61);
        if (index == -1) {
            return s.substring(1);
        }
        return s.substring(1, index);
    }
    
    private String parseValue(final String s) {
        final int index = s.indexOf(61);
        if (index == -1) {
            return null;
        }
        return s.substring(index + 1);
    }
}
