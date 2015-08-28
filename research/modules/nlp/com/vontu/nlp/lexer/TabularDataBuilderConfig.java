// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;

public class TabularDataBuilderConfig
{
    public static final String ALLOW_COMMA_SEPARATOR = "allow_comma_separator";
    public static final String ENABLE_MULTI_WORD_RECOGNITION = "enable_multi_word_recognition";
    public static final String INCLUDE_LINES_WITH_WORDS_ONLY = "include_lines_with_words_only";
    public static final String INCLUDE_POSTAL_CODE_IN_MULTI_WORD = "include_postal_code_in_multi_word";
    private final boolean _allowCommasWithOtherSeparators;
    private final boolean _isMultiWordRecognitionEnabled;
    private final boolean _includePostalCodeInMultiWord;
    private final boolean _includeLinesWithWordsOnly;
    
    public TabularDataBuilderConfig(final SettingProvider settingProvider) {
        this(new SettingReader((SettingProvider)new TabularDataBuilderSettingProvider(settingProvider)));
    }
    
    public TabularDataBuilderConfig(final SettingReader settingReader) {
        this._allowCommasWithOtherSeparators = settingReader.getBooleanSetting("allow_comma_separator");
        this._isMultiWordRecognitionEnabled = settingReader.getBooleanSetting("enable_multi_word_recognition");
        this._includeLinesWithWordsOnly = settingReader.getBooleanSetting("include_lines_with_words_only");
        this._includePostalCodeInMultiWord = settingReader.getBooleanSetting("include_postal_code_in_multi_word");
    }
    
    public boolean allowCommasWithOtherSeparators() {
        return this._allowCommasWithOtherSeparators;
    }
    
    public boolean isMultiWordRecognitionEnabled() {
        return this._isMultiWordRecognitionEnabled;
    }
    
    public boolean includePostalCodeInMultiWord() {
        return this._includePostalCodeInMultiWord;
    }
    
    public boolean includeLinesWithWordsOnly() {
        return this._includeLinesWithWordsOnly;
    }
}
