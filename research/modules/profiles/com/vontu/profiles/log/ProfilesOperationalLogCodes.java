// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.log;

import com.vontu.logging.operational.api.OperationalLogCategory;
import com.vontu.logging.operational.api.OperationalLogCode;

public class ProfilesOperationalLogCodes
{
    public static final OperationalLogCode IDM_INDEXER_STARTING;
    public static final OperationalLogCode IDM_INDEXER_COMPLETE;
    public static final OperationalLogCode IDM_INDEXER_FAILED;
    
    static {
        IDM_INDEXER_STARTING = new OperationalLogCode(OperationalLogCategory.IDM_INDEXER, 1);
        IDM_INDEXER_COMPLETE = new OperationalLogCode(OperationalLogCategory.IDM_INDEXER, 2);
        IDM_INDEXER_FAILED = new OperationalLogCode(OperationalLogCategory.IDM_INDEXER, 3);
    }
}
