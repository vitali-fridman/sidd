// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import java.io.IOException;
import java.io.DataInputStream;

interface IndexEntryFactory
{
    int estimateMemory(int p0, int p1, int p2);
    
    IndexEntry read(DataInputStream p0, int p1) throws IOException;
}
