// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.ramindex.util;

public final class AccuracyCalculator
{
    public static double estimateFalsePositiveRate(final int termSize, final int averageBucketSize, final int rowCount, final int columnCount, final int textProximityRadius) {
        final double singleTermMatches = averageBucketSize / (1L << 8 * termSize);
        final double queryMatches = singleTermMatches * textProximityRadius * 2.0;
        if (columnCount > 1) {
            final long tupleCount = (queryMatches >= columnCount) ? numberOfCombinations((int)queryMatches, columnCount) : 0L;
            final double sameRowProbability = Math.pow(rowCount, 1 - columnCount);
            final double allMatchProbability = (singleTermMatches >= 1.0) ? 1.0 : Math.pow(singleTermMatches, columnCount);
            final double allMatchSameRowProbability = sameRowProbability * allMatchProbability;
            return 1.0 - Math.pow(1.0 - allMatchSameRowProbability, tupleCount);
        }
        return (queryMatches < 1.0) ? queryMatches : 1.0;
    }
    
    private static long numberOfCombinations(final int setSize, final int tupleSize) {
        long tupleSizeFactorial = 1L;
        for (int i = 1; i <= tupleSize; ++i) {
            tupleSizeFactorial *= i;
        }
        long result = 1L;
        for (int j = setSize - tupleSize + 1; j <= setSize; ++j) {
            result *= j;
        }
        return result / tupleSizeFactorial;
    }
}
