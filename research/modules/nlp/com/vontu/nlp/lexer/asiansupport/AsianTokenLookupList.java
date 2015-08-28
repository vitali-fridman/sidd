// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.asiansupport;

import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import com.vontu.util.unicode.UnicodeNormalizerRegistry;
import java.util.HashMap;
import com.vontu.util.unicode.AsianTokenUtil;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AsianTokenLookupList
{
    private Map<Character, List<Integer>> _lookup;
    private static final Collection<Integer> EMPTY_LIST;
    private final AsianTokenUtil _asianTokenUtil;
    
    public AsianTokenLookupList() {
        this._lookup = new HashMap<Character, List<Integer>>();
        this._asianTokenUtil = UnicodeNormalizerRegistry.getAsianTokenUtils();
    }
    
    public void addUnhashedCharater(char c, final int length) {
        if (!this._asianTokenUtil.isAsianCharacter(c)) {
            return;
        }
        c = AsianTokenUtil.hashChar(c);
        this.addHashedCharacter(c, length);
    }
    
    public synchronized void addHashedCharacter(final char c, final int length) {
        List<Integer> list;
        if (!this._lookup.containsKey(c)) {
            list = new ArrayList<Integer>();
            this._lookup.put(c, list);
        }
        else {
            list = this._lookup.get(c);
        }
        if (!list.contains(length)) {
            list.add(length);
        }
    }
    
    public Collection<Integer> getLengthListFromUnhashedChar(final Character c) {
        return this.getLengthList(c, false);
    }
    
    public Collection<Integer> getLengthList(Character c, final boolean isAlreadyHashed) {
        if (!isAlreadyHashed) {
            if (!this._asianTokenUtil.isAsianCharacter((char)c)) {
                return null;
            }
            c = AsianTokenUtil.hashChar((char)c);
        }
        final Collection<Integer> collect = this._lookup.get(c);
        return (collect == null) ? collect : Collections.unmodifiableCollection((Collection<? extends Integer>)collect);
    }
    
    public Set<Character> getAllHashedCharacters() {
        return Collections.unmodifiableSet((Set<? extends Character>)this._lookup.keySet());
    }
    
    public int getSize() {
        return this._lookup.size();
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        final Set<Character> chars = this.getAllHashedCharacters();
        out.append("[#:");
        out.append(chars.size());
        out.append("]");
        for (final char c : chars) {
            out.append("[");
            final String val = Integer.toHexString(c);
            out.append("0000".substring(val.length()));
            out.append(val);
            out.append(":");
            boolean first = true;
            for (final int i : this.getLengthList(c, true)) {
                if (!first) {
                    out.append(",");
                }
                first = false;
                out.append(i);
            }
            out.append("]");
        }
        return out.toString();
    }
    
    static {
        EMPTY_LIST = new ArrayList<Integer>();
    }
}
