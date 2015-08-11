// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.util.List;
import com.vontu.util.config.ConfigurationException;
import java.util.StringTokenizer;
import java.util.LinkedList;
import com.vontu.util.text.Interval;
import com.vontu.util.config.SettingProvider;
import cern.colt.list.IntArrayList;

public class AsianTokenUtil
{
    public static final int HASHED_MAX = 16384;
    private final IntArrayList _asianCharLookup;
    
    public AsianTokenUtil() {
        this(new DefaultSettings());
    }
    
    public AsianTokenUtil(final SettingProvider settings) {
        String asianCharRanges = settings.getSetting("UnicodeNormalizer.AsianCharRanges");
        if (asianCharRanges == null || asianCharRanges.equalsIgnoreCase("default")) {
            asianCharRanges = new DefaultSettings().getSetting("UnicodeNormalizer.AsianCharRanges");
        }
        final Interval[] intervals = this.parseIntervals(asianCharRanges);
        this._asianCharLookup = this.createLookup(intervals);
    }
    
    private IntArrayList createLookup(final Interval[] intervals) {
        final IntArrayList asianCharLookup = new IntArrayList();
        for (final Interval interval : intervals) {
            for (int curr = interval.getLow(); curr <= interval.getHigh(); ++curr) {
                asianCharLookup.add(curr);
            }
        }
        asianCharLookup.trimToSize();
        asianCharLookup.sortFromTo(0, asianCharLookup.size() - 1);
        return asianCharLookup;
    }
    
    private Interval[] parseIntervals(final String val) {
        final List<Interval> intervals = new LinkedList<Interval>();
        final StringTokenizer tokens = new StringTokenizer(val, " \t-,", false);
        int codePoint = -1;
        while (tokens.hasMoreTokens()) {
            final String tok = tokens.nextToken();
            try {
                if (codePoint < 0) {
                    codePoint = Integer.parseInt(tok, 16);
                }
                else {
                    intervals.add(new Interval(codePoint, Integer.parseInt(tok, 16)));
                    codePoint = -1;
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new ConfigurationException("Error parsing Asian Character Range definition");
            }
        }
        return intervals.toArray(new Interval[0]);
    }
    
    public boolean isAsianCharacter(final char c) {
        return this.isAsianCodePoint(c);
    }
    
    public boolean isAsianCodePoint(final int codePoint) {
        return this._asianCharLookup.binarySearchFromTo(codePoint, 0, this._asianCharLookup.size() - 1) >= 0;
    }
    
    public static char hashChar(char c) {
        c &= '\u3fff';
        char out = (char)((c & '\u001f') << 9);
        out |= (char)(c >> 5 & '\u01ff');
        out ^= '\u2456';
        return out;
    }
}
