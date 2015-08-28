// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.jflex;

import java.io.IOException;
import com.vontu.util.ProtectException;
import com.vontu.nlp.lexer.TokenValidationException;
import com.vontu.nlp.lexer.TextToken;
import com.vontu.nlp.lexer.DBDataToken;
import com.vontu.nlp.lexer.TokenPosition;
import com.vontu.nlp.lexer.Token;
import java.io.Reader;
import com.vontu.util.CharSequenceReader;
import com.vontu.nlp.lexer.LexerConfiguration;
import com.vontu.nlp.lexer.Lexer;

public class JFlexWrapper
{
    private Lexer _lexer;
    private JFlexCallback _callback;
    private CharSequence _content;
    private boolean _isValidating;
    private JFlexLexer _scanner;
    private boolean _isIndexing;
    public static final int LENGTH_NOT_SET = 0;
    
    public JFlexWrapper(final Lexer lexer, final CharSequence content, final boolean isValidating, final LexerConfiguration config) {
        this(lexer, (Reader)new CharSequenceReader(content), content, isValidating, config);
    }
    
    public JFlexWrapper(final Lexer lexer, final Reader reader, final CharSequence content, final boolean isValidating, final LexerConfiguration config) {
        this._lexer = lexer;
        this._content = content;
        this._isValidating = isValidating;
        this._isIndexing = (content == null);
        this._scanner = new JFlexLexer(new NewLineEndingReader(reader));
        this._callback = new JFlexCallback(this, this._isIndexing, config.getAsianTokenMergedLookup());
        this._scanner.setCallback(this._callback);
        if (config.includePunctuationInWords()) {
            this._scanner.yybegin(1);
        }
        else {
            this._scanner.yybegin(2);
        }
    }
    
    public Token createToken(final int type, boolean validate, final int failedValidationType, final int yychar, final int yyline, final int yycolumn, int length) {
        String text = null;
        if (this._isIndexing) {
            text = this._scanner.yytext();
            length = this._scanner.yylength();
        }
        else if (length == 0) {
            length = this._scanner.yylength();
        }
        if (!this._isValidating) {
            validate = false;
        }
        Token token = null;
        final TokenPosition tokenPosition = new TokenPosition(yychar, length, yyline, yycolumn);
        try {
            token = (this._isIndexing ? new DBDataToken(text, type, tokenPosition, validate) : new TextToken(this._content, type, tokenPosition, validate));
            return token;
        }
        catch (TokenValidationException e) {
            token = (this._isIndexing ? new DBDataToken(text, failedValidationType, tokenPosition) : new TextToken(this._content, failedValidationType, tokenPosition));
            return token;
        }
        finally {
            if (token == null) {
                throw new OutOfMemoryError("Out of memory creating new Token.");
            }
            return token;
        }
    }
    
    public void registerToken(final Token token) throws ProtectException, InterruptedException {
        if (this._isIndexing) {
            this._lexer.addToken(token);
        }
        else {
            this._lexer.addTextToken((TextToken)token);
        }
    }
    
    public void runJFlexLexer() throws IOException, ProtectException, InterruptedException {
        this._scanner.run();
    }
}
