// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.math.BigInteger;

public class CombinationGenerator
{
    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;
    
    public CombinationGenerator(final int n, final int r) {
        if (r > n) {
            throw new IllegalArgumentException();
        }
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.r = r;
        this.a = new int[r];
        final BigInteger nFact = getFactorial(n);
        final BigInteger rFact = getFactorial(r);
        final BigInteger nminusrFact = getFactorial(n - r);
        this.total = nFact.divide(rFact.multiply(nminusrFact));
        this.reset();
    }
    
    public void reset() {
        for (int i = 0; i < this.a.length; ++i) {
            this.a[i] = i;
        }
        this.numLeft = new BigInteger(this.total.toString());
    }
    
    public BigInteger getNumLeft() {
        return this.numLeft;
    }
    
    public boolean hasMore() {
        return this.numLeft.compareTo(BigInteger.ZERO) == 1;
    }
    
    public BigInteger getTotal() {
        return this.total;
    }
    
    private static BigInteger getFactorial(final int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; --i) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }
    
    public int[] getNext() {
        if (this.numLeft.equals(this.total)) {
            this.numLeft = this.numLeft.subtract(BigInteger.ONE);
            return this.a;
        }
        int i;
        for (i = this.r - 1; this.a[i] == this.n - this.r + i; --i) {}
        ++this.a[i];
        for (int j = i + 1; j < this.r; ++j) {
            this.a[j] = this.a[i] + j - i;
        }
        this.numLeft = this.numLeft.subtract(BigInteger.ONE);
        return this.a;
    }
}
