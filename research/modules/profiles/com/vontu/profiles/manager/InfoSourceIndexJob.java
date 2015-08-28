// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager;

import com.vontu.model.DataAccessException;
import org.quartz.JobExecutionException;
import com.vontu.model.security.SystemUser;
import java.util.logging.Level;
import org.quartz.JobExecutionContext;
import java.util.logging.Logger;
import org.quartz.StatefulJob;

public abstract class InfoSourceIndexJob implements StatefulJob
{
    public static final String INFO_SOURCE_IDS = "datasource.ids";
    private final Logger _logger;
    
    public InfoSourceIndexJob() {
        this._logger = Logger.getLogger(InfoSourceIndexJob.class.getName());
    }
    
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        this._logger.log(Level.INFO, "Indexing InfoSource Job begin executing");
        try {
            SystemUser.runAsSystemUser();
        }
        catch (DataAccessException e) {
            throw new JobExecutionException((Throwable)e, false);
        }
        String triggerName = "notrigger";
        if (context != null && context.getTrigger() != null && context.getTrigger().getKey().getName() != null) {
            triggerName = context.getTrigger().getKey().getName();
        }
        final int[] dataSourceIds = (int[])context.getJobDetail().getJobDataMap().get((Object)"datasource.ids");
        this.index(triggerName, dataSourceIds);
    }
    
    protected abstract void index(final String p0, final int[] p1);
}
