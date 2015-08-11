// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

public class XMLCharacterFilter implements CodePointFilter
{
    private static final char INVALID_CHARACTER_SUBSTITUTE = ' ';
    
    public String filterText(final String originalText) {
        if (originalText == null) {
            return null;
        }
        return this.filterSequence(originalText).toString();
    }
    
    @Override
    public CharSequence filterSequence(final CharSequence originalText) {
        if (originalText == null) {
            return null;
        }
        final XMLFilteringBuffer outputBuffer = new XMLFilteringBuffer(this, originalText);
        while (!outputBuffer.isComplete()) {
            outputBuffer.addNextCharacter();
        }
        return outputBuffer.getResult();
    }
    
    @Override
    public boolean accepts(final int codePoint) {
        return (codePoint >= 32 && codePoint <= 55295) || (codePoint >= 57344 && codePoint <= 65533) || codePoint == 10 || (codePoint >= 65536 && codePoint <= 1114111) || codePoint == 9 || codePoint == 13;
    }
    
    @Override
    public char getReplacementCharacter() {
        return ' ';
    }
    
    private static class XMLFilteringBuffer
    {
        private final CharSequence original;
        private final XMLCharacterFilter filter;
        private StringBuilder builder;
        private int index;
        
        public XMLFilteringBuffer(final XMLCharacterFilter filter, final CharSequence original) {
            this.original = original;
            this.filter = filter;
            this.index = 0;
        }
        
        public boolean isComplete() {
            return this.index >= this.original.length();
        }
        
        public void addNextCharacter() {
            final int codePoint = Character.codePointAt(this.original, this.index);
            final int codePointSize = Character.charCount(codePoint);
            if (this.filter.accepts(codePoint)) {
                this.appendCharacters(codePointSize);
            }
            else {
                this.appendSubstituteChar(codePointSize);
            }
        }
        
        private void appendCharacters(final int charCount) {
            if (this.builder != null) {
                this.builder.append(this.original, this.index, this.index + charCount);
            }
            this.index += charCount;
        }
        
        private void appendSubstituteChar(final int codePointSize) {
            if (this.builder == null) {
                (this.builder = new StringBuilder(this.original.length())).append(this.original.subSequence(0, this.index));
            }
            this.builder.append(' ');
            this.index += codePointSize;
        }
        
        public CharSequence getResult() {
            return (this.builder != null) ? this.builder.toString() : this.original;
        }
    }
}
