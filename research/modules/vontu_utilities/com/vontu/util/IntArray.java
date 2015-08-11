// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public class IntArray
{
    int[] _array;
    int _increment;
    int _current;
    
    public IntArray() {
        this(1024, 1024);
    }
    
    public IntArray(final int initSize, final int increment) {
        if (initSize < 0 || increment <= 0) {
            throw new IllegalArgumentException();
        }
        this._array = new int[initSize];
        this._increment = 1024;
        this._current = -1;
    }
    
    public void add(final int elem) {
        if (this._current == this._array.length - 1) {
            final int[] temp = this._array;
            System.arraycopy(temp, 0, this._array = new int[temp.length + this._increment], 0, temp.length);
        }
        ++this._current;
        this._array[this._current] = elem;
    }
    
    public int getElementAt(final int index) {
        if (index > this._current) {
            throw new IllegalArgumentException();
        }
        return this._array[index];
    }
    
    public int length() {
        return this._current + 1;
    }
    
    public int[] toIntArray() {
        if (this._current == -1) {
            return null;
        }
        final int[] result = new int[this.length()];
        System.arraycopy(this._array, 0, result, 0, this.length());
        return result;
    }
}
