// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.database;

import java.util.HashMap;
import com.vontu.util.CharArrayCharSequence;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.util.ProtectException;
import java.util.logging.Level;
import com.vontu.nlp.lexer.TokenizedContent;
import java.util.Collection;
import com.vontu.nlp.lexer.LexerSpecification;
import java.util.Collections;
import com.vontu.util.config.SettingProvider;
import com.vontu.nlp.lexer.TabularDataBuilder;
import com.vontu.util.config.MapSettingProvider;
import com.vontu.nlp.lexer.Lexer;
import java.util.Map;
import java.util.logging.Logger;
import com.vontu.profileindex.tokenizer.TokenizedContentFactory;

final class PredicateOperandTokenFactory implements TokenizedContentFactory
{
    private static final CharSequence NO_CONTENT;
    private static final Logger _logger;
    private static final Map<String, String> _lexerSettings;
    private static final Map<String, String> _tabularDataBuilderSettings;
    private final Lexer _lexer;
    
    PredicateOperandTokenFactory() {
        this._lexer = new Lexer(new LexerSpecification((SettingProvider)new MapSettingProvider((Map)PredicateOperandTokenFactory._lexerSettings), (Collection)Collections.singleton(new TabularDataBuilder((SettingProvider)new MapSettingProvider((Map)PredicateOperandTokenFactory._tabularDataBuilderSettings)))));
    }
    
    @Override
    public TokenizedContent newInstance(final CharSequence textContent) {
        try {
            final TokenizedContent tc = this._lexer.run(textContent);
            if (PredicateOperandTokenFactory._logger.isLoggable(Level.FINEST)) {
                PredicateOperandTokenFactory._logger.finest("TokenizedContent in PredicateOperandTokenFactory:\n" + tc.toString());
            }
            return tc;
        }
        catch (ProtectException e) {
            PredicateOperandTokenFactory._logger.log(Level.SEVERE, "Failed to tokenize content.", (Throwable)e);
            return new TokenizedContent(PredicateOperandTokenFactory.NO_CONTENT, 0);
        }
        catch (InterruptedException e2) {
            PredicateOperandTokenFactory._logger.warning("Skipping tokenization because the thread has been interrupted.");
            Thread.currentThread().interrupt();
            return new TokenizedContent(PredicateOperandTokenFactory.NO_CONTENT, 0);
        }
    }
    
    @Override
    public boolean loadDescriptor(final ProfileIndexDescriptor descriptor) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @Override
    public boolean unloadDescriptor(final ProfileIndexDescriptor descriptor) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    static {
        NO_CONTENT = (CharSequence)new CharArrayCharSequence(new char[0]);
        _logger = Logger.getLogger(PredicateOperandTokenFactory.class.getName());
        _lexerSettings = new HashMap<String, String>();
        _tabularDataBuilderSettings = new HashMap<String, String>();
        PredicateOperandTokenFactory._lexerSettings.put("enable_full_validation", "false");
        PredicateOperandTokenFactory._lexerSettings.put("include_punctuation_in_words", "true");
        PredicateOperandTokenFactory._lexerSettings.put("maximum_number_of_tokens", "100");
        PredicateOperandTokenFactory._tabularDataBuilderSettings.put("allow_comma_separator", "true");
        PredicateOperandTokenFactory._tabularDataBuilderSettings.put("enable_multi_word_recognition", "true");
        PredicateOperandTokenFactory._tabularDataBuilderSettings.put("include_lines_with_words_only", "true");
        PredicateOperandTokenFactory._tabularDataBuilderSettings.put("include_postal_code_in_multi_word", "true");
    }
}
