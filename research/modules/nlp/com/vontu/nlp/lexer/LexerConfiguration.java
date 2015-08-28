// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.config.ConfigurationException;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import com.vontu.nlp.lexer.asiansupport.AsianTokenMergedLookup;
import java.io.File;
import java.util.logging.Logger;

public class LexerConfiguration
{
    public static final String INCLUDE_PUNCTUATION_IN_WORDS = "include_punctuation_in_words";
    public static final boolean INCLUDE_PUNCTUATION_IN_WORDS_DEFAULT = true;
    public static final String MAX_NUM_TOKENS = "maximum_number_of_tokens";
    public static final int MAX_NUM_TOKENS_DEFAULT = 30000;
    public static final String STOPWORD_DICTIONARY_FOLDER = "stopword_dictionary_folder";
    public static final String STOPWORD_LANGUAGES = "stopword_languages";
    public static final String ENABLE_FULL_VALIDATION = "enable_full_validation";
    public static final boolean ENABLE_FULL_VALIDATION_DEFAULT = true;
    private static final Logger _logger;
    private final boolean _isFullValidationEnabled;
    private final int _maxNumberOfTokens;
    private final boolean _includePunctuationInWords;
    private final String _stopwordLanguages;
    private final File _stopwordDictionaryFolder;
    private AsianTokenMergedLookup _asianTokenLookup;
    
    public LexerConfiguration(final SettingProvider settingProvider) throws ConfigurationException {
        this(settingProvider, new SettingReader(settingProvider, LexerConfiguration._logger));
    }
    
    private LexerConfiguration(final SettingProvider settingProvider, final SettingReader reader) throws ConfigurationException {
        this._includePunctuationInWords = reader.getBooleanSetting("include_punctuation_in_words", true);
        this._isFullValidationEnabled = reader.getBooleanSetting("enable_full_validation", true);
        this._maxNumberOfTokens = reader.getIntSetting("maximum_number_of_tokens", 30000);
        this._stopwordLanguages = settingProvider.getSetting("stopword_languages");
        final String stopwordDictionaryFolderPath = settingProvider.getSetting("stopword_dictionary_folder");
        this._stopwordDictionaryFolder = ((stopwordDictionaryFolderPath == null) ? null : new File(stopwordDictionaryFolderPath));
        this._asianTokenLookup = null;
    }
    
    public boolean isFullValidationEnabled() {
        return this._isFullValidationEnabled;
    }
    
    public int getMaximumNumberOfTokens() {
        return this._maxNumberOfTokens;
    }
    
    public File getStopwordDictionaryFolder() {
        return this._stopwordDictionaryFolder;
    }
    
    public String getStopwordLanguages() {
        return this._stopwordLanguages;
    }
    
    public boolean includePunctuationInWords() {
        return this._includePunctuationInWords;
    }
    
    public AsianTokenMergedLookup getAsianTokenMergedLookup() {
        return this._asianTokenLookup;
    }
    
    public void setAsianTokenMergedLookup(final AsianTokenMergedLookup asianTokenMergedLookup) {
        this._asianTokenLookup = asianTokenMergedLookup;
    }
    
    @Override
    public String toString() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append("enable_full_validation").append('=').append(this.isFullValidationEnabled());
        stringBuilder.append(lineSeparator);
        stringBuilder.append("include_punctuation_in_words").append('=').append(this.includePunctuationInWords());
        stringBuilder.append(lineSeparator);
        stringBuilder.append("maximum_number_of_tokens").append('=').append(this.getMaximumNumberOfTokens());
        stringBuilder.append(lineSeparator);
        stringBuilder.append("stopword_dictionary_folder").append('=');
        stringBuilder.append((this.getStopwordDictionaryFolder() == null) ? "<unspecified>" : this.getStopwordDictionaryFolder().getAbsolutePath());
        stringBuilder.append(lineSeparator);
        stringBuilder.append("stopword_languages").append('=');
        stringBuilder.append((this.getStopwordLanguages() == null) ? "<unspecified>" : this.getStopwordLanguages());
        return stringBuilder.toString();
    }
    
    static {
        _logger = Logger.getLogger(LexerConfiguration.class.getName());
    }
}
