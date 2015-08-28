// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import java.util.HashMap;
import java.util.Map;
import com.vontu.util.ProtectError;

public class ProfilesError extends ProtectError
{
    private static final int BEGIN = 1000;
    public static final ProfilesError PROFILES_DATABASE;
    public static final ProfilesError PROFILES_DATABASE_CONNECTION;
    public static final ProfilesError PROFILES_CANCELLED;
    public static final ProfilesError PROFILES_STATUS_LOCKED;
    public static final ProfilesError PROFILES_CORRUPTED;
    public static final ProfilesError PROFILES_WRITE_ERROR;
    public static final ProfilesError PROCESS_SHUTDOWN;
    private static final Map<ProtectError, Integer> _statusMap;
    
    protected static void addStatusMapping(final ProfilesError error, final int indexedDataStatus) {
        ProfilesError._statusMap.put(error, indexedDataStatus);
    }
    
    protected ProfilesError(final int value, final String description) throws IllegalArgumentException {
        super(value, description);
    }
    
    public int toIndexedDataStatus() {
        return toStatus(this);
    }
    
    private static Integer getStatus(final ProtectError error) {
        return ProfilesError._statusMap.get(error);
    }
    
    public static int toStatus(final ProtectError error) {
        return (getStatus(error) != null) ? getStatus(error) : 16;
    }
    
    static {
        PROFILES_DATABASE = new ProfilesError(1002, "Manager database error occurred");
        PROFILES_DATABASE_CONNECTION = new ProfilesError(1009, "Error connecting to manager database");
        PROFILES_CANCELLED = new ProfilesError(1013, "Indexing was cancelled");
        PROFILES_STATUS_LOCKED = new ProfilesError(1014, "Index status object locked.");
        PROFILES_CORRUPTED = new ProfilesError(1015, "Index result is corrupted");
        PROFILES_WRITE_ERROR = new ProfilesError(1016, "Unable to rename index files");
        PROCESS_SHUTDOWN = new ProfilesError(1115, "Indexing stopped due to process shutdown");
        _statusMap = new HashMap<ProtectError, Integer>();
        addStatusMapping(ProfilesError.PROFILES_CANCELLED, 8);
        addStatusMapping(ProfilesError.PROFILES_DATABASE, 13);
        addStatusMapping(ProfilesError.PROFILES_DATABASE_CONNECTION, 13);
        addStatusMapping(ProfilesError.PROCESS_SHUTDOWN, 14);
    }
}
