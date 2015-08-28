// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex.tokenizer;

import com.vontu.util.CharArrayCharSequence;
import java.io.IOException;
import com.vontu.profileindex.ProfileIndexDescriptor;
import com.vontu.util.ProtectException;
import java.util.logging.Level;
import com.vontu.nlp.lexer.Lexer;
import java.util.Collection;
import com.vontu.nlp.lexer.LexerSpecification;
import java.util.Collections;
import com.vontu.nlp.lexer.TabularDataBuilder;
import com.vontu.nlp.lexer.TokenizedContent;
import com.vontu.util.Stopwatch;
import com.vontu.profileindex.database.AsianTokenListManager;
import com.vontu.nlp.lexer.LexerConfiguration;
import com.vontu.nlp.lexer.TabularDataBuilderConfig;
import com.vontu.util.config.SettingProvider;
import java.util.logging.Logger;

public class TokenizedDetectionContentFactory implements TokenizedContentFactory
{
    private static final Logger _logger;
    private static final CharSequence NO_CONTENT;
    private final SettingProvider _settings;
    private final TabularDataBuilderConfig _tabularBuilderConfig;
    private final LexerConfiguration _lexerConfig;
    private final AsianTokenListManager _asianTokenListManager;
    private final Stopwatch _stopwatch;
    
    public TokenizedDetectionContentFactory(final SettingProvider settingProvider) {
        this._stopwatch = new Stopwatch("TokenizedDetectionContentFactory");
        this._settings = settingProvider;
        this._lexerConfig = new LexerConfiguration(settingProvider);
        this._tabularBuilderConfig = new TabularDataBuilderConfig(settingProvider);
        this._asianTokenListManager = new AsianTokenListManager();
    }
    
    @Override
    public TokenizedContent newInstance(final CharSequence textContent) {
        try {
            final LexerSpecification lexerSpec = new LexerSpecification(this._lexerConfig, (Collection)Collections.singleton(new TabularDataBuilder(this._tabularBuilderConfig)));
            lexerSpec.getConfig().setAsianTokenMergedLookup(this._asianTokenListManager.getAsianTokenMergedLookup());
            final Lexer lexer = new Lexer(lexerSpec);
            if (TokenizedDetectionContentFactory._logger.isLoggable(Level.FINE)) {
                this._stopwatch.start();
            }
            final TokenizedContent tokenizedContent = lexer.run(textContent);
            if (TokenizedDetectionContentFactory._logger.isLoggable(Level.FINEST)) {
                TokenizedDetectionContentFactory._logger.finest("TokenizedContent in TokenDetectionContentFactory:\n" + tokenizedContent.toString());
            }
            if (TokenizedDetectionContentFactory._logger.isLoggable(Level.FINE)) {
                TokenizedDetectionContentFactory._logger.fine("Content tokenization took " + this._stopwatch.stop().getLastTime() + " milliseconds.");
            }
            return tokenizedContent;
        }
        catch (ProtectException e) {
            TokenizedDetectionContentFactory._logger.log(Level.SEVERE, "Failed to tokenize content.", (Throwable)e);
            return new TokenizedContent(TokenizedDetectionContentFactory.NO_CONTENT, 0);
        }
        catch (InterruptedException e2) {
            TokenizedDetectionContentFactory._logger.warning("Skipping tokenization because the thread has been interrupted.");
            Thread.currentThread().interrupt();
            return new TokenizedContent(TokenizedDetectionContentFactory.NO_CONTENT, 0);
        }
    }
    
    @Override
    public boolean loadDescriptor(final ProfileIndexDescriptor descriptor) {
        try {
            this._asianTokenListManager.load(descriptor);
            return true;
        }
        catch (IOException e) {
            TokenizedDetectionContentFactory._logger.log(Level.SEVERE, "Unable to load descriptor: " + descriptor, e);
            return false;
        }
    }
    
    @Override
    public boolean unloadDescriptor(final ProfileIndexDescriptor descriptor) {
        this._asianTokenListManager.unload(descriptor);
        return true;
    }
    
    static {
        _logger = Logger.getLogger(TokenizedDetectionContentFactory.class.getName());
        NO_CONTENT = (CharSequence)new CharArrayCharSequence(new char[0]);
    }
}
