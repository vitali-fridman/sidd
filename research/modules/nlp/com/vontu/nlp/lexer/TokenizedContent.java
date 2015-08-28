// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer;

import com.vontu.util.ProtectException;
import com.vontu.nlp.lexer.pattern.SystemPattern;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class TokenizedContent
{
    private static final int TOKENS_INITIAL_CAPACITY = 2000;
    private static final int BY_TYPE_INITIAL_CAPACITY = 1000;
    private final List[] _byTypeTokens;
    private final CharSequence _content;
    private final int _maxNumTokens;
    private Map _nfaPatternMatches;
    private final List _tokens;
    private final TokenSetCollection[] _tokenSets;
    
    public TokenizedContent(final CharSequence content, final int maxNumberOfTokens) {
        this._byTypeTokens = new List[16];
        this._tokens = new ArrayList(2000);
        this._tokenSets = new TokenSetCollection[1];
        for (int i = 0; i < this._byTypeTokens.length; ++i) {
            this._byTypeTokens[i] = new ArrayList(1000);
        }
        for (int i = 0; i < this._tokenSets.length; ++i) {
            this._tokenSets[i] = new TokenSetCollection(i);
        }
        this._content = content;
        this._maxNumTokens = maxNumberOfTokens;
    }
    
    public void releaseReferences() {
        final Iterator it = this._tokens.iterator();
        while (it.hasNext()) {
            it.next().releaseReferences();
        }
    }
    
    public CharSequence getCharContent() {
        return this._content;
    }
    
    public List getTokens() {
        return Collections.unmodifiableList((List<?>)this._tokens);
    }
    
    void setNFApatternMatches(final Map nfaPatternMatches) {
        this._nfaPatternMatches = nfaPatternMatches;
    }
    
    public List getTokenSets(final int type) {
        return this._tokenSets[type].getTokenSets();
    }
    
    public Iterator getPatternMatchIterator(final Pattern pattern) {
        if (this._nfaPatternMatches == null) {
            throw new IllegalStateException("Pattern matches has not been set.");
        }
        final List matches = this._nfaPatternMatches.get(pattern.pattern());
        if (matches == null) {
            throw new IllegalArgumentException("Pattern: " + pattern.pattern() + " was not part of the Lexer specification.");
        }
        return Collections.unmodifiableList((List<?>)matches).iterator();
    }
    
    public List getTokensByType(final int tokenType) {
        return Collections.unmodifiableList((List<?>)this._byTypeTokens[tokenType]);
    }
    
    public List getTokens(final SystemPattern systemPattern) throws IllegalArgumentException {
        if (systemPattern == SystemPattern.CCN) {
            return this.getTokensByType(4);
        }
        if (systemPattern == SystemPattern.TAXID) {
            return this.getTokensByType(3);
        }
        if (systemPattern == SystemPattern.IP_ADDRESS) {
            return this.getTokensByType(8);
        }
        if (systemPattern == SystemPattern.EMAIL) {
            return this.getTokensByType(2);
        }
        if (systemPattern == SystemPattern.POSTAL_CODE) {
            return this.getTokensByType(9);
        }
        if (systemPattern == SystemPattern.PHONE) {
            return this.getTokensByType(1);
        }
        if (systemPattern == SystemPattern.PERCENT) {
            return this.getTokensByType(7);
        }
        throw new IllegalArgumentException(systemPattern + " system patter isn't supported.");
    }
    
    void addToken(final TextToken token) throws ProtectException {
        if (this._tokens.size() == this._maxNumTokens) {
            throw new TooManyTokensException(this._maxNumTokens);
        }
        this._tokens.add(token);
        final int index = this._tokens.size() - 1;
        token.setIndex(index);
        this._byTypeTokens[token.getType()].add(token);
    }
    
    void setTokenSetCollection(final int type, final TokenSetCollection collection) {
        this._tokenSets[type] = collection;
    }
    
    @Override
    public String toString() {
        if (this._tokens == null || this._tokenSets == null) {
            return "<not initialized>";
        }
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuffer stringBuilder = new StringBuffer(2048);
        stringBuilder.append("TokenizedContent contains ").append(this._tokens.size()).append(" tokens");
        stringBuilder.append(lineSeparator);
        Iterator it = this._tokens.iterator();
        while (it.hasNext()) {
            stringBuilder.append(it.next()).append(lineSeparator);
        }
        for (int i = 0; i < 1; ++i) {
            if (this._tokenSets[i] != null) {
                stringBuilder.append(TokenSetType.getName(i)).append(": ");
                stringBuilder.append(this._tokenSets[i].size()).append(" sets").append(lineSeparator);
                final Iterator iter = this._tokenSets[i].getTokenSets().iterator();
                while (iter.hasNext()) {
                    stringBuilder.append("   Set:").append(lineSeparator);
                    stringBuilder.append(iter.next().toString());
                }
            }
        }
        if (this._nfaPatternMatches != null) {
            it = this._nfaPatternMatches.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry entry = it.next();
                final String pattern = entry.getKey();
                final List matches = entry.getValue();
                stringBuilder.append("-----------").append(matches.size());
                stringBuilder.append(" matches of NFA pattern: ").append(pattern).append(lineSeparator);
                final Iterator mit = matches.iterator();
                while (mit.hasNext()) {
                    stringBuilder.append(mit.next().toString()).append(lineSeparator);
                }
            }
        }
        return stringBuilder.toString();
    }
    
    public void addPattern(final Pattern pattern) {
        if (this._nfaPatternMatches == null) {
            throw new IllegalStateException("addPattern() is called before Lexer.run()");
        }
        final NFAPatternMatcher matcher = new NFAPatternMatcher(pattern);
        matcher.setDfaTokens(this.getTokens());
        final List matches = matcher.search(this._content);
        this._nfaPatternMatches.put(pattern.pattern(), matches);
    }
}
