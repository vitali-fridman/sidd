// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor;

import com.vontu.profiles.common.ProfilesError;

public final class ProfileMonitorError extends ProfilesError
{
    public static final ProfilesError PROFILE_MONITOR_FOLDER_PROBLEM;
    public static final ProfilesError PROFILE_MONITOR_FILECOPY_ERROR;
    public static final ProfilesError PROFILE_MONITOR_RENAME_ERROR;
    
    private ProfileMonitorError(final int value, final String description) {
        super(value, description);
    }
    
    static {
        PROFILE_MONITOR_FOLDER_PROBLEM = new ProfileMonitorError(1006, "Unable to use index working directory");
        PROFILE_MONITOR_FILECOPY_ERROR = new ProfileMonitorError(1007, "Error replicating the remote index file");
        PROFILE_MONITOR_RENAME_ERROR = new ProfileMonitorError(1010, "Error renaming index file");
        ProfilesError.addStatusMapping(ProfileMonitorError.PROFILE_MONITOR_FOLDER_PROBLEM, 12);
        ProfilesError.addStatusMapping(ProfileMonitorError.PROFILE_MONITOR_FILECOPY_ERROR, 12);
        ProfilesError.addStatusMapping(ProfileMonitorError.PROFILE_MONITOR_RENAME_ERROR, 12);
    }
}
