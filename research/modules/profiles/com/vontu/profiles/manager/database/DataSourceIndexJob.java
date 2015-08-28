// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.database;

import com.vontu.profiles.manager.InfoSourceIndexCreator;
import com.vontu.profiles.manager.InfoSourceIndexJob;

public class DataSourceIndexJob extends InfoSourceIndexJob
{
    @Override
    protected void index(final String triggerName, final int[] infoSourceIDs) {
        InfoSourceIndexCreator.indexListOfDataSources(triggerName, infoSourceIDs);
    }
}
