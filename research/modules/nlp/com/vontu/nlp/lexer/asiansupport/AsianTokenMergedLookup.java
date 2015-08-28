// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.nlp.lexer.asiansupport;

import com.vontu.util.unicode.AsianTokenUtil;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AsianTokenMergedLookup
{
    private static final Set<Integer> EMPTY;
    private List<AsianTokenLookupList> _lookupLists;
    private HashMap<Character, TreeSet<Integer>> _lengths;
    private Set<Integer>[] _lengthFastLookup;
    private static final int LENGTH_ARRAY_SIZE = 16384;
    
    public AsianTokenMergedLookup() {
        this._lookupLists = new ArrayList<AsianTokenLookupList>();
        this._lengths = new HashMap<Character, TreeSet<Integer>>();
        this._lengthFastLookup = (Set<Integer>[])new Set[16384];
    }
    
    public synchronized void addLookupList(final AsianTokenLookupList list) {
        for (final char hashedChar : list.getAllHashedCharacters()) {
            if (this._lengths.get(hashedChar) == null) {
                final TreeSet<Integer> newSet = new TreeSet<Integer>(list.getLengthList(hashedChar, true));
                this._lengths.put(hashedChar, newSet);
                this._lengthFastLookup[hashedChar] = Collections.unmodifiableSet((Set<? extends Integer>)newSet);
            }
            else {
                final TreeSet<Integer> currentLengths = this._lengths.get(hashedChar);
                for (final Integer i : list.getLengthList(hashedChar, true)) {
                    currentLengths.add(i);
                }
            }
        }
        this._lookupLists.add(list);
    }
    
    public synchronized void removeLookupList(final AsianTokenLookupList list) {
        if (!this._lookupLists.contains(list)) {
            throw new IllegalArgumentException("List not part of the existing lists.");
        }
        this._lookupLists.remove(list);
        for (final char hashedChar : list.getAllHashedCharacters()) {
            final TreeSet<Integer> redone = new TreeSet<Integer>();
            for (final AsianTokenLookupList current : this._lookupLists) {
                final Collection<Integer> lengths = current.getLengthList(hashedChar, true);
                if (lengths != null) {
                    redone.addAll(lengths);
                }
            }
            if (redone.size() == 0) {
                this._lengths.remove(hashedChar);
                this._lengthFastLookup[hashedChar] = null;
            }
            else {
                if (redone.size() == this._lengths.get(hashedChar).size()) {
                    continue;
                }
                this._lengths.remove(hashedChar);
                this._lengths.put(hashedChar, redone);
                this._lengthFastLookup[hashedChar] = Collections.unmodifiableSet((Set<? extends Integer>)redone);
            }
        }
    }
    
    public Set<Integer> getLengths(final char unhashedCharacter) {
        final char c = AsianTokenUtil.hashChar(unhashedCharacter);
        return (this._lengthFastLookup[c] == null) ? AsianTokenMergedLookup.EMPTY : this._lengthFastLookup[c];
    }
    
    public int getNumLists() {
        return this._lookupLists.size();
    }
    
    @Override
    public String toString() {
        return "Lists: " + this.getNumLists();
    }
    
    static {
        EMPTY = Collections.unmodifiableSet((Set<? extends Integer>)Collections.EMPTY_SET);
    }
}
