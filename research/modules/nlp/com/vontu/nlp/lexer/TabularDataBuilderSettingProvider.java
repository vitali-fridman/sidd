// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.HashMap;
import com.vontu.util.config.MapSettingProvider;
import com.vontu.util.config.SettingProvider;
import java.util.Map;
import com.vontu.util.config.FallbackSettingProvider;

class TabularDataBuilderSettingProvider extends FallbackSettingProvider
{
    public static final String ALLOW_COMMA_SEPARATOR_DEFAULT = "true";
    public static final String ENABLE_MULTI_WORD_RECOGNITION_DEFAULT = "true";
    public static final String INCLUDE_LINES_WITH_WORDS_ONLY_DEFAULT = "false";
    public static final String INCLUDE_POSTAL_CODE_IN_MULTI_WORD_DEFULAT = "true";
    private static final Map<String, String> _values;
    
    protected TabularDataBuilderSettingProvider(final SettingProvider mainProvider) {
        super(mainProvider, (SettingProvider)new MapSettingProvider((Map)TabularDataBuilderSettingProvider._values));
    }
    
    static {
        (_values = new HashMap<String, String>()).put("allow_comma_separator", "true");
        TabularDataBuilderSettingProvider._values.put("enable_multi_word_recognition", "true");
        TabularDataBuilderSettingProvider._values.put("include_lines_with_words_only", "false");
        TabularDataBuilderSettingProvider._values.put("include_postal_code_in_multi_word", "true");
    }
}
