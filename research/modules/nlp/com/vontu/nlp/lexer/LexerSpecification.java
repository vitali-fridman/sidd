// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Collections;
import com.vontu.util.config.SettingProvider;
import java.util.Collection;

public class LexerSpecification
{
    private final Collection<String> _patterns;
    private final Collection<TokenSetBuilder> _tokenSetBuilders;
    private final boolean _shouldValidate;
    private final LexerConfiguration _config;
    
    public LexerConfiguration getConfig() {
        return this._config;
    }
    
    public Collection<String> getPatterns() {
        return this._patterns;
    }
    
    public Collection<TokenSetBuilder> getTokenSetBuilders() {
        return this._tokenSetBuilders;
    }
    
    public boolean isValidating() {
        return this._shouldValidate;
    }
    
    public LexerSpecification(final SettingProvider settingProvider) {
        this(settingProvider, Collections.EMPTY_LIST);
    }
    
    public LexerSpecification(final SettingProvider settingProvider, final Collection tokenSetBuilders) {
        this(settingProvider, tokenSetBuilders, Collections.EMPTY_LIST);
    }
    
    public LexerSpecification(final LexerConfiguration lexerConfig, final Collection tokenSetBuilders) {
        this(lexerConfig, tokenSetBuilders, Collections.EMPTY_LIST);
    }
    
    public LexerSpecification(final SettingProvider settingProvider, final Collection tokenSetBuilders, final Collection patterns) {
        this(new LexerConfiguration(settingProvider), tokenSetBuilders, patterns);
    }
    
    public LexerSpecification(final LexerConfiguration lexerConfig, final Collection tokenSetBuilders, final Collection patterns) {
        this._config = lexerConfig;
        this._patterns = (Collection<String>)patterns;
        this._tokenSetBuilders = (Collection<TokenSetBuilder>)tokenSetBuilders;
        this._shouldValidate = this._config.isFullValidationEnabled();
    }
}
