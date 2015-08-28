// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

public final class ProfilesConnectionException extends ProfilesException
{
    public ProfilesConnectionException() {
        super(ProfilesError.PROFILES_DATABASE_CONNECTION);
    }
    
    public ProfilesConnectionException(final String message) {
        super(ProfilesError.PROFILES_DATABASE_CONNECTION, message);
    }
    
    public ProfilesConnectionException(final Throwable cause) {
        super(ProfilesError.PROFILES_DATABASE_CONNECTION, cause);
    }
    
    public ProfilesConnectionException(final Throwable cause, final String message) {
        super(ProfilesError.PROFILES_DATABASE_CONNECTION, cause, message);
    }
}
