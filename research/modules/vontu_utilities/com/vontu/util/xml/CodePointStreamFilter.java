// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

public class CodePointStreamFilter
{
    private final CodePointFilter filter;
    private boolean hasPreviousHighSurrogate;
    private char highSurrogate;
    
    public CodePointStreamFilter(final CodePointFilter filter) {
        this.hasPreviousHighSurrogate = false;
        this.filter = filter;
    }
    
    public CharSequence filterNextSequence(final CharSequence text) {
        if (text.length() > 0) {
            final char lastChar = text.charAt(text.length() - 1);
            CharSequence filteredText = this.resolvePartialCodePoint(text);
            filteredText = this.filterText(filteredText);
            filteredText = this.storePartialCodePoint(filteredText, lastChar);
            return filteredText;
        }
        return text;
    }
    
    public char getReplacementCharacter() {
        return this.filter.getReplacementCharacter();
    }
    
    public void reset() {
        this.hasPreviousHighSurrogate = false;
    }
    
    public boolean hasRemainingCharacter() {
        return this.hasPreviousHighSurrogate;
    }
    
    private CharSequence resolvePartialCodePoint(final CharSequence text) {
        if (this.hasPreviousHighSurrogate) {
            return new PrependedCharSequence(this.highSurrogate, text);
        }
        return text;
    }
    
    private CharSequence filterText(final CharSequence text) {
        return this.filter.filterSequence(text);
    }
    
    private CharSequence storePartialCodePoint(final CharSequence filteredText, final char originalLastChar) {
        if (Character.isHighSurrogate(originalLastChar)) {
            this.hasPreviousHighSurrogate = true;
            this.highSurrogate = originalLastChar;
            return filteredText.subSequence(0, filteredText.length() - 1);
        }
        this.hasPreviousHighSurrogate = false;
        return filteredText;
    }
}
