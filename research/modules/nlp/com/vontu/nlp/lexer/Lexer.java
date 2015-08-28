// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.unicode.UnicodeNormalizingReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.vontu.util.ExceptionHandlers;
import java.io.IOException;
import com.vontu.util.ProtectError;
import com.vontu.nlp.lexer.jflex.JFlexWrapper;
import com.vontu.util.ProtectException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class Lexer
{
    private static final Logger _logger;
    private final LexerConfiguration _config;
    private final Collection _patterns;
    private final Collection _matchers;
    private final TokenSetBuilder[] _builders;
    private final boolean _isValidating;
    private TokenizedContent _tokenizedContent;
    
    public Lexer(final LexerSpecification spec) throws PatternSyntaxException {
        WordNormalizer.createInstance(this._config = spec.getConfig());
        this._patterns = spec.getPatterns();
        this._matchers = new ArrayList(this._patterns.size());
        for (final String pattern : this._patterns) {
            this._matchers.add(new NFAPatternMatcher(pattern));
        }
        this._builders = new TokenSetBuilder[1];
        for (final TokenSetBuilder builder : spec.getTokenSetBuilders()) {
            this._builders[builder.getType()] = builder;
        }
        this._isValidating = this._config.isFullValidationEnabled();
        if (Lexer._logger.isLoggable(Level.FINE)) {
            final String lineSeparator = System.getProperty("line.separator");
            final StringBuffer logRecord = new StringBuffer(this.getClass().getName()).append(" created with configuration:");
            logRecord.append(lineSeparator);
            logRecord.append(this._config);
            logRecord.append(lineSeparator);
            logRecord.append(this._patterns.size()).append(" NFA regular expression(s): ");
            for (final Object pattern2 : this._patterns) {
                logRecord.append(lineSeparator).append('\t').append(pattern2);
            }
            logRecord.append(lineSeparator);
            logRecord.append(this._builders.length).append(" token set builder(s): ");
            for (final TokenSetBuilder builder2 : this._builders) {
                if (builder2 != null) {
                    logRecord.append(lineSeparator).append('\t').append(builder2.getDescriptiveName());
                }
            }
            Lexer._logger.fine(logRecord.toString());
        }
    }
    
    public void compile() {
    }
    
    public static boolean assertNFAPatternIsValid(final String expression) {
        if (expression == null && expression.length() == 0) {
            return false;
        }
        try {
            Pattern.compile(expression);
        }
        catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }
    
    public TokenizedContent run(final CharSequence content) throws ProtectException, InterruptedException {
        this._tokenizedContent = new TokenizedContent(content, this._config.getMaximumNumberOfTokens());
        return this.run();
    }
    
    private TokenizedContent run() throws ProtectException, InterruptedException {
        final JFlexWrapper scanner = new JFlexWrapper(this, this._tokenizedContent.getCharContent(), this._isValidating, this._config);
        for (int i = 0; i < 1; ++i) {
            if (this._builders[i] != null) {
                this._builders[i].initialize();
            }
        }
        try {
            scanner.runJFlexLexer();
        }
        catch (IOException e) {
            throw new ProtectException((ProtectError)LexerError.JFLEX_ERROR, (Throwable)e, "IOException while running JFlex scanner");
        }
        catch (TooManyTokensException e2) {
            if (Lexer._logger.isLoggable(Level.FINE)) {
                Lexer._logger.fine("Reached maximum number of tokens (" + e2.maximumNumberOfTokens() + ").");
            }
        }
        catch (OutOfMemoryError e3) {
            ExceptionHandlers.handleError(e3, "Out of memory running JFlex scanner.");
        }
        for (int i = 0; i < 1; ++i) {
            if (this._builders[i] != null) {
                this._builders[i].done(this._tokenizedContent);
                this._tokenizedContent.setTokenSetCollection(i, this._builders[i].getTokenSetCollection());
            }
        }
        final Map map = new HashMap();
        for (final NFAPatternMatcher matcher : this._matchers) {
            matcher.setDfaTokens(this._tokenizedContent.getTokens());
            final List matches = matcher.search(this._tokenizedContent.getCharContent());
            map.put(matcher.getExpression(), matches);
        }
        this._tokenizedContent.setNFApatternMatches(map);
        if (Lexer._logger.isLoggable(Level.FINE)) {
            final String lineSeparator = System.getProperty("line.separator");
            final StringBuffer messageBuilder = new StringBuffer(1024);
            messageBuilder.append("The first two lines of original content are: ").append(lineSeparator);
            messageBuilder.append(this._tokenizedContent.getCharContent(), 0, getFirstNLinesOfText(this._tokenizedContent.getCharContent(), 2));
            messageBuilder.append(lineSeparator);
            messageBuilder.append("Tokenized content is ");
            messageBuilder.append(this._tokenizedContent.getCharContent().length());
            messageBuilder.append(" characters long.");
            messageBuilder.append(lineSeparator);
            messageBuilder.append("Tokenized content contains ");
            messageBuilder.append(this._tokenizedContent.getTokens().size()).append(" tokens and ");
            messageBuilder.append(this._tokenizedContent.getTokenSets(0).size());
            messageBuilder.append(" tabular data set(s).");
            Lexer._logger.fine(messageBuilder.toString());
        }
        if (Lexer._logger.isLoggable(Level.FINEST)) {
            final String lineSeparator = System.getProperty("line.separator");
            Lexer._logger.finest("Original content:" + lineSeparator + this._tokenizedContent.getCharContent().toString());
            Lexer._logger.finest("Tokenized content:" + lineSeparator + this._tokenizedContent.toString());
        }
        return this._tokenizedContent;
    }
    
    private static int getFirstNLinesOfText(final CharSequence text, final int lines) {
        int i = 0;
        for (int nLines = 0; i < text.length() && nLines < lines; ++i) {
            if (text.charAt(i) == '\n') {
                ++nLines;
            }
        }
        return i;
    }
    
    public void run(final Reader reader) throws ProtectException, InterruptedException {
        final JFlexWrapper scanner = new JFlexWrapper(this, (Reader)new UnicodeNormalizingReader(reader), null, this._isValidating, this._config);
        for (int i = 0; i < 1; ++i) {
            if (this._builders[i] != null) {
                this._builders[i].initialize();
            }
        }
        try {
            scanner.runJFlexLexer();
        }
        catch (IOException e) {
            throw new ProtectException((ProtectError)LexerError.JFLEX_ERROR, (Throwable)e, "IOException while running JFlex scanner");
        }
        catch (Error e2) {
            throw new ProtectException((ProtectError)LexerError.JFLEX_ERROR, (Throwable)e2, "Error while running JFlex scanner");
        }
        for (int i = 0; i < 1; ++i) {
            if (this._builders[i] != null) {
                this._builders[i].done();
            }
        }
    }
    
    public void addTextToken(final TextToken token) throws ProtectException, InterruptedException {
        this._tokenizedContent.addToken(token);
        this.addToken(token);
    }
    
    public void addToken(final Token token) throws ProtectException, InterruptedException {
        for (int i = 0; i < 1; ++i) {
            if (this._builders[i] != null) {
                this._builders[i].process(token);
            }
        }
    }
    
    static {
        _logger = Logger.getLogger(Lexer.class.getName());
    }
}
