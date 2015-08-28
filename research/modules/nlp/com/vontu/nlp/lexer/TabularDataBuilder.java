// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import java.util.Iterator;
import java.util.Collection;
import com.vontu.util.config.SettingReader;
import com.vontu.util.config.SettingProvider;
import java.util.ArrayList;

public class TabularDataBuilder extends TokenSetBuilder
{
    private ArrayList[] _tokensPerLine;
    private RowSignature[] _signatures;
    private static final String DESCRIPTIVE_NAME = "Tabular Data Recognizer";
    private final TabularDataBuilderConfig _config;
    
    public TabularDataBuilder(final SettingProvider settingProvider) {
        this(new TabularDataBuilderConfig(settingProvider));
    }
    
    public TabularDataBuilder(final SettingReader settingReader) {
        this(new TabularDataBuilderConfig(settingReader));
    }
    
    public TabularDataBuilder(final TabularDataBuilderConfig config) {
        super(0);
        this._config = config;
    }
    
    @Override
    public void initialize() {
        super.initialize();
    }
    
    @Override
    public void done(final TokenizedContent tokenizedContent) {
        super.done(tokenizedContent);
        this._tokensPerLine = separatePerLine(tokenizedContent.getTokens());
        this._signatures = this.getSignatures(this._tokensPerLine);
        this.goFindTabular();
    }
    
    private void goFindTabular() {
        int firstLine = -1;
        int lastLine = -1;
        String baseSignature = "-1";
        for (int i = 0; i < this._signatures.length; ++i) {
            if (this._signatures[i].signature.length() > 0 && this._signatures[i].signature.equals(baseSignature)) {
                ++lastLine;
            }
            else {
                if (lastLine > firstLine) {
                    this.foundTabularData(firstLine, lastLine);
                }
                firstLine = i;
                lastLine = i;
                baseSignature = this._signatures[i].signature;
            }
        }
        if (lastLine > firstLine) {
            this.foundTabularData(firstLine, lastLine);
        }
    }
    
    private void foundTabularData(final int firstTabularLine, final int lastTabularLine) {
        final TokenSet set = new TabularTokenSet(lastTabularLine - firstTabularLine + 1);
        for (int i = firstTabularLine; i <= lastTabularLine; ++i) {
            final Collection row = new ArrayList();
            int tokenBetweenSeparatorCounter = 0;
            int firstTokenIndex = -1;
            TokenPosition firstTokenPosition = null;
            StringBuffer normalizedBuffer = new StringBuffer();
            final Iterator it = this._tokensPerLine[i].iterator();
            TextToken token = null;
            if (it.hasNext()) {
                token = it.next();
            }
            TextToken nextToken = null;
            if (it.hasNext()) {
                nextToken = it.next();
            }
            while (token != null) {
                if ((!this._config.allowCommasWithOtherSeparators() && token.getType() == 12) || (this._config.allowCommasWithOtherSeparators() && !token.getValue().equals(this._signatures[i].separator))) {
                    if (token.getType() != 11) {
                        String value = null;
                        if (token.hasNormalizedValues()) {
                            value = token.getTrueLongestNormalizedValue();
                        }
                        else {
                            value = token.getValue();
                        }
                        if (value != null && value.length() > 0) {
                            normalizedBuffer = normalizedBuffer.append(value);
                            if (nextToken != null && nextToken.getType() != 12 && token.getType() != 12) {
                                normalizedBuffer = normalizedBuffer.append(" ");
                            }
                        }
                        row.add(token);
                    }
                    ++tokenBetweenSeparatorCounter;
                }
                if (tokenBetweenSeparatorCounter == 1) {
                    firstTokenIndex = token.getIndex();
                    firstTokenPosition = token.getPosition();
                }
                if ((!this._config.allowCommasWithOtherSeparators() && token.getType() == 12) || (this._config.allowCommasWithOtherSeparators() && token.getValue().equals(this._signatures[i].separator)) || !it.hasNext()) {
                    if (tokenBetweenSeparatorCounter > 1 && normalizedBuffer.length() > 0) {
                        final int length = token.getPosition().start - firstTokenPosition.start + token.getPosition().length;
                        final TokenPosition position = new TokenPosition(firstTokenPosition.start, length, firstTokenPosition.line, firstTokenPosition.column);
                        final TextToken multiToken = new TextToken(token.getCharContent(), 0, position);
                        multiToken.setIndex(firstTokenIndex);
                        multiToken.setNormalizedValues(normalizedBuffer.toString());
                        row.add(multiToken);
                    }
                    normalizedBuffer = new StringBuffer();
                    tokenBetweenSeparatorCounter = 0;
                    firstTokenIndex = -1;
                    firstTokenPosition = null;
                }
                token = nextToken;
                if (it.hasNext()) {
                    nextToken = it.next();
                }
                else {
                    nextToken = null;
                }
            }
            set.add(row);
        }
        this._tokenSetCollection.add(set);
    }
    
    private static ArrayList[] separatePerLine(final Collection tokens) {
        ArrayList[] perLine = null;
        final TextToken[] textTokens = tokens.toArray(new TextToken[tokens.size()]);
        if (textTokens.length > 0) {
            final int lastLine = textTokens[textTokens.length - 1].getPosition().line;
            perLine = new ArrayList[lastLine + 1];
            for (int i = 0; i <= lastLine; ++i) {
                perLine[i] = new ArrayList();
            }
        }
        else {
            perLine = new ArrayList[0];
        }
        for (int j = 0; j < textTokens.length; ++j) {
            perLine[textTokens[j].getPosition().line].add(textTokens[j]);
        }
        return perLine;
    }
    
    private RowSignature[] getSignatures(final ArrayList[] tokensPerLine) {
        final RowSignature[] signatures = new RowSignature[tokensPerLine.length];
        for (int i = 0; i < tokensPerLine.length; ++i) {
            final StringBuffer sb = new StringBuffer();
            String separator = this._config.allowCommasWithOtherSeparators() ? this.figureOutSeparator(tokensPerLine[i].iterator()) : null;
            for (final TextToken token : tokensPerLine[i]) {
                final char code = TokenType.getCode(token.getType());
                switch (code) {
                    case 'q': {
                        break;
                    }
                    case 's': {
                        if (this._config.allowCommasWithOtherSeparators()) {
                            final String tokenValue = token.getValue();
                            if (tokenValue.equals(separator)) {
                                sb.append(code);
                            }
                            else {
                                this.defaultSignatureProcessing(sb, code);
                            }
                            break;
                        }
                        if (separator == null) {
                            separator = token.getValue();
                            sb.append(code);
                            break;
                        }
                        if (token.getValue().equals(separator)) {
                            sb.append(code);
                            break;
                        }
                        sb.delete(0, sb.length());
                        sb.append('0');
                        break;
                    }
                    default: {
                        this.defaultSignatureProcessing(sb, code);
                        break;
                    }
                }
                if (sb.length() > 0 && sb.charAt(0) == '0') {
                    sb.delete(0, sb.length());
                    break;
                }
            }
            boolean interresting = false;
            for (int j = 0; j < sb.length(); ++j) {
                if (sb.charAt(j) != 'z' && sb.charAt(j) != 's') {
                    if (this._config.includeLinesWithWordsOnly()) {
                        interresting = true;
                        break;
                    }
                    if (sb.charAt(j) != 'w') {
                        interresting = true;
                        break;
                    }
                }
            }
            if (!interresting) {
                sb.delete(0, sb.length());
            }
            for (int j = 0; j < sb.length(); ++j) {
                if (sb.charAt(j) == 'z') {
                    sb.setCharAt(j, 'w');
                }
            }
            signatures[i] = new RowSignature(sb.toString(), separator);
        }
        return signatures;
    }
    
    private void defaultSignatureProcessing(final StringBuffer sb, final char code) {
        final int length = sb.length();
        if (length > 0) {
            final char prevCode = sb.charAt(length - 1);
            switch (prevCode) {
                case 's': {
                    sb.append(code);
                    break;
                }
                case 'z': {
                    if (code == 'w') {
                        break;
                    }
                    if (code == 'n') {
                        break;
                    }
                    if (this._config.includePostalCodeInMultiWord() && code == 'o') {
                        break;
                    }
                    if (code == 's' && this._config.allowCommasWithOtherSeparators()) {
                        break;
                    }
                    sb.setCharAt(0, '0');
                    break;
                }
                case 'o': {
                    if (!this._config.includePostalCodeInMultiWord()) {
                        sb.delete(0, sb.length());
                        sb.append('0');
                        break;
                    }
                }
                case 'n':
                case 'w': {
                    if (!this._config.isMultiWordRecognitionEnabled()) {
                        sb.delete(0, sb.length());
                        sb.append('0');
                        break;
                    }
                    if (code == 'w' || code == 'n') {
                        sb.setCharAt(length - 1, 'z');
                        break;
                    }
                    if (this._config.includePostalCodeInMultiWord() && code == 'o') {
                        sb.setCharAt(length - 1, 'z');
                        break;
                    }
                    if (code == 's' && this._config.allowCommasWithOtherSeparators()) {
                        sb.setCharAt(length - 1, 'z');
                        break;
                    }
                    sb.delete(0, sb.length());
                    sb.append('0');
                    break;
                }
                default: {
                    sb.delete(0, sb.length());
                    sb.append('0');
                    break;
                }
            }
        }
        else {
            sb.append(code);
        }
    }
    
    private String figureOutSeparator(final Iterator tokens) {
        boolean nonCommaFound = false;
        String separator = null;
        while (tokens.hasNext() && !nonCommaFound) {
            final TextToken token = tokens.next();
            if (token.getType() == 12) {
                final String tokenValue = token.getValue();
                if (tokenValue.indexOf(44) == -1) {
                    nonCommaFound = true;
                }
                separator = tokenValue;
            }
        }
        return separator;
    }
    
    @Override
    public void process(final Token token) {
    }
    
    @Override
    public String getDescriptiveName() {
        return "Tabular Data Recognizer";
    }
}
