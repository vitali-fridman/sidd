// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.process;

public class SixtyFourOrThirtyTwoBitDeterminer
{
    private static final String SIXTY_FOUR_BIT;
    
    boolean isSixtyFourBitJVM() {
        return SixtyFourOrThirtyTwoBitDeterminer.SIXTY_FOUR_BIT.equals(System.getProperty("sun.arch.data.model"));
    }
    
    static {
        SIXTY_FOUR_BIT = new String("64");
    }
}
