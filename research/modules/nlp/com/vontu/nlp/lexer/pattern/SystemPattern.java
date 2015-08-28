// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.pattern;

import java.util.HashMap;
import java.util.Map;

public class SystemPattern
{
    private static final Map _namePatternMap;
    public static final SystemPattern CCN;
    public static final SystemPattern EMAIL;
    public static final SystemPattern IP_ADDRESS;
    public static final SystemPattern NUMBER;
    public static final SystemPattern PERCENT;
    public static final SystemPattern PHONE;
    public static final SystemPattern POSTAL_CODE;
    public static final SystemPattern TAXID;
    private final String _name;
    private final SystemPatternMatcher _patternMatcher;
    
    protected SystemPattern(final String name, final SystemPatternMatcher patternMatcher) {
        this._name = name;
        this._patternMatcher = patternMatcher;
        SystemPattern._namePatternMap.put(name, this);
    }
    
    public static SystemPattern forName(final String name) {
        return SystemPattern._namePatternMap.get(name);
    }
    
    public SystemPatternMatcher getPatternMatcher() {
        return this._patternMatcher;
    }
    
    public Object name() {
        return this._name;
    }
    
    @Override
    public String toString() {
        return this._name;
    }
    
    static {
        _namePatternMap = new HashMap();
        CCN = new SystemPattern("CCN", new CcnPatternMatcher());
        EMAIL = new SystemPattern("EMAIL", new EmailPatternMatcher());
        IP_ADDRESS = new SystemPattern("IP_ADDRESS", new IpAddressPatternMatcher());
        NUMBER = new SystemPattern("NUMBER", new NumberPatternMatcher());
        PERCENT = new SystemPattern("PERCENT", new PercentPatternMatcher());
        PHONE = new SystemPattern("PHONE", new PhonePatternMatcher());
        POSTAL_CODE = new SystemPattern("POSTAL_CODE", new PostalCodePatternMatcher());
        TAXID = new SystemPattern("TAXID", new TaxIdPatternMatcher());
    }
}
