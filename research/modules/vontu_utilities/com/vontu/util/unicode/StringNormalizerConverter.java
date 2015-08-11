// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import com.vontu.util.collection.Converter;

public class StringNormalizerConverter implements Converter<String, String>
{
    private StringNormalizer _normalizer;
    
    public StringNormalizerConverter(final StringNormalizer normalizer) {
        this._normalizer = normalizer;
    }
    
    @Override
    public String convert(final String source) {
        return this._normalizer.normalize(source).toString();
    }
}
