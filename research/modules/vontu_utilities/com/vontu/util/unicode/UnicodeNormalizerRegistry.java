// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

public class UnicodeNormalizerRegistry
{
    private static UnicodeNormalizerRegistry _instance;
    private final UnicodeNormalizer _normalizer;
    private final AsianTokenUtil _asianTokenUtil;
    
    public static synchronized UnicodeNormalizerRegistry getInstance() {
        return UnicodeNormalizerRegistry._instance;
    }
    
    public static synchronized void setInstance(final UnicodeNormalizerRegistry newInstance) {
        UnicodeNormalizerRegistry._instance = newInstance;
    }
    
    public static void initializeDefaultInstance() {
        setInstance(new UnicodeNormalizerRegistry(new UnicodeNormalizer(), new AsianTokenUtil()));
    }
    
    public UnicodeNormalizerRegistry(final UnicodeNormalizer normalizer, final AsianTokenUtil asianTokenUtil) {
        this._normalizer = normalizer;
        this._asianTokenUtil = asianTokenUtil;
    }
    
    public static UnicodeNormalizer getNormalizer() {
        return getInstance()._normalizer;
    }
    
    public static AsianTokenUtil getAsianTokenUtils() {
        return getInstance()._asianTokenUtil;
    }
}
