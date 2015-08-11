// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

class PrependedCharSequence implements CharSequence
{
    private final char prepend;
    private final CharSequence original;
    
    public PrependedCharSequence(final char prepend, final CharSequence original) {
        this.prepend = prepend;
        this.original = original;
    }
    
    @Override
    public char charAt(final int index) {
        if (index == 0) {
            return this.prepend;
        }
        return this.original.charAt(index - 1);
    }
    
    @Override
    public int length() {
        return this.original.length() + 1;
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        if (start > 0) {
            return this.original.subSequence(start - 1, end - 1);
        }
        if (start == 0 && end == 0) {
            return "";
        }
        if (start == 0) {
            return new PrependedCharSequence(this.prepend, this.original.subSequence(0, end - 1));
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.original.length() + 1);
        builder.append(this.prepend);
        builder.append(this.original);
        return builder.toString();
    }
}
