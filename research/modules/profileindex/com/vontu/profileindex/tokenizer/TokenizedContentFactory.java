// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.tokenizer;

import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.nlp.lexer.TokenizedContent;

public interface TokenizedContentFactory
{
    TokenizedContent newInstance(CharSequence p0);
    
    boolean loadDescriptor(ProfileIndexDescriptor p0);
    
    boolean unloadDescriptor(ProfileIndexDescriptor p0);
}
