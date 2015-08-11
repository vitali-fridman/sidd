// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.unicode;

import java.text.CharacterIterator;

class CharSequenceIterator implements CharacterIterator
{
    private final CharSequence _sequence;
    private int _position;
    
    CharSequenceIterator(final CharSequence sequence) {
        this(sequence, 0);
    }
    
    private CharSequenceIterator(final CharSequence sequence, final int position) {
        this._sequence = sequence;
        this._position = position;
    }
    
    @Override
    public char first() {
        this.setIndex(this.getBeginIndex());
        return this.current();
    }
    
    @Override
    public char last() {
        this.setIndex((this.getEndIndex() == 0) ? 0 : (this.getEndIndex() - 1));
        return (this.getEndIndex() == 0) ? '\uffff' : this.current();
    }
    
    @Override
    public char current() {
        return (this.getIndex() == this.getEndIndex()) ? '\uffff' : this._sequence.charAt(this.getIndex());
    }
    
    @Override
    public char next() {
        this.setIndex((this.getIndex() + 1 >= this.getEndIndex()) ? this.getEndIndex() : (this.getIndex() + 1));
        return this.current();
    }
    
    @Override
    public char previous() {
        if (this.getIndex() == this.getBeginIndex()) {
            return '\uffff';
        }
        this.setIndex(this.getIndex() - 1);
        return this.current();
    }
    
    @Override
    public char setIndex(final int position) {
        if (position < this.getBeginIndex() || position > this.getEndIndex()) {
            throw new IllegalArgumentException();
        }
        this._position = position;
        return this.current();
    }
    
    @Override
    public int getBeginIndex() {
        return 0;
    }
    
    @Override
    public int getEndIndex() {
        return this._sequence.length();
    }
    
    @Override
    public int getIndex() {
        return this._position;
    }
    
    @Override
    public CharacterIterator clone() {
        try {
            return (CharacterIterator)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Impossible");
        }
    }
    
    @Override
    public String toString() {
        return this.current() + "@" + this.getIndex();
    }
}
