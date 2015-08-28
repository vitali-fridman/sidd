// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.docindex.algorithm;

import java.text.Normalizer;

public class IDMEndpointNormalizer
{
    private ContentNormalizer contentNormalizer;
    
    public IDMEndpointNormalizer() {
        this.contentNormalizer = new ContentNormalizer();
    }
    
    public CharSequence normalize(final String textToNormalize) {
        String unicodeNormalizedText = null;
        if (!Normalizer.isNormalized(textToNormalize, Normalizer.Form.NFKC)) {
            unicodeNormalizedText = Normalizer.normalize(textToNormalize, Normalizer.Form.NFKC);
        }
        else {
            unicodeNormalizedText = textToNormalize;
        }
        final CharSequence normalizedText = this.contentNormalizer.normalize(unicodeNormalizedText);
        return normalizedText;
    }
}
