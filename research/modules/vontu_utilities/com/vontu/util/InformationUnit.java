// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

public enum InformationUnit
{
    BYTES(0), 
    KB(1), 
    MB(2), 
    GB(3), 
    TB(4), 
    PB(5), 
    EB(6);
    
    private static final long MINIMUM_COUNT = Long.MIN_VALUE;
    private static final long MAXIMUM_COUNT = Long.MAX_VALUE;
    private final int scaleFactor;
    
    private static long scale(final long value, final long factor, final long maximumValue) {
        if (value > maximumValue) {
            return Long.MAX_VALUE;
        }
        if (value < -maximumValue) {
            return Long.MIN_VALUE;
        }
        return value * factor;
    }
    
    private static long pow(final int exponent) {
        return (long)Math.floor(Math.pow(1024.0, exponent));
    }
    
    private InformationUnit(final int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    public long convert(final long sourceCount, final InformationUnit sourceUnit) {
        final int scaleDifference = this.scaleFactor - sourceUnit.scaleFactor;
        if (scaleDifference == 0) {
            return sourceCount;
        }
        if (scaleDifference > 0) {
            return sourceCount / pow(scaleDifference);
        }
        final long factor = pow(-scaleDifference);
        return scale(sourceCount, factor, Long.MAX_VALUE / factor);
    }
    
    public double convertToDouble(final long sourceCount, final InformationUnit sourceUnit) {
        final int scaleDifference = this.scaleFactor - sourceUnit.scaleFactor;
        if (scaleDifference == 0) {
            return sourceCount;
        }
        if (scaleDifference > 0) {
            return sourceCount / pow(scaleDifference);
        }
        final long factor = pow(-scaleDifference);
        return scale(sourceCount, factor, Long.MAX_VALUE / factor);
    }
}
