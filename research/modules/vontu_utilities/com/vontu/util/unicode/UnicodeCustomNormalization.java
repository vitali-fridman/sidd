// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

public class UnicodeCustomNormalization
{
    private final String _to;
    private final String _from;
    
    public UnicodeCustomNormalization(final String from, final String to) {
        this._from = from;
        this._to = to;
    }
    
    public String getFrom() {
        return this._from;
    }
    
    public String getTo() {
        return this._to;
    }
    
    public boolean match(final CharSequence seq, int startPos) {
        if (seq.length() - startPos < this._from.length()) {
            return false;
        }
        for (int i = 0; i < this._from.length(); ++i) {
            if (this._from.charAt(i) != seq.charAt(startPos)) {
                return false;
            }
            ++startPos;
        }
        return true;
    }
}
