// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.io.Serializable;

public class CharArraySlice implements Serializable
{
    private static final long serialVersionUID = -4824048043100318302L;
    private final char[] _chars;
    private final int _start;
    private final int _end;
    
    public CharArraySlice(final char[] chars, final int start, final int end) {
        if (start < 0 || end > chars.length || start > end) {
            throw new IllegalArgumentException("Invalid range");
        }
        this._chars = chars;
        this._start = start;
        this._end = end;
    }
    
    public char[] allChars() {
        return this._chars;
    }
    
    public int start() {
        return this._start;
    }
    
    public int end() {
        return this._end;
    }
}
