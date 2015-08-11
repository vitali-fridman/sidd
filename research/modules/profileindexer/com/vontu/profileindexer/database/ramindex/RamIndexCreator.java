// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database.ramindex;

import com.vontu.profileindexer.IndexerException;
import java.io.File;

public interface RamIndexCreator
{
    RamIndexResult createRamIndex(CryptoIndexDescriptor p0, File p1) throws IndexerException, InterruptedException;
}
