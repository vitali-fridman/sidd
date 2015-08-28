// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.common;

import com.vontu.util.ProtectError;
import java.io.Serializable;
import com.vontu.util.ProtectException;

public class ProfilesException extends ProtectException implements Serializable
{
    public ProfilesException(final ProfilesError error) {
        super((ProtectError)error);
    }
    
    public ProfilesException(final ProfilesError error, final String message) {
        super((ProtectError)error, message);
    }
    
    public ProfilesException(final ProfilesError error, final Throwable cause) {
        super((ProtectError)error, cause);
    }
    
    public ProfilesException(final ProfilesError error, final Throwable cause, final String message) {
        super((ProtectError)error, cause, message);
    }
    
    public ProfilesError getIndexError() {
        return (ProfilesError)this.getError();
    }
}
