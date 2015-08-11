// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.security;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessControlContext;
import javax.security.auth.Subject;
import java.security.PrivilegedAction;
import java.security.Principal;

public class PrivilegedActionExecutor<T>
{
    public T execute(final Principal principal, final PrivilegedAction<T> action) throws SecurityException {
        final Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        return Subject.doAsPrivileged(subject, action, null);
    }
    
    public T execute(final Principal principal, final PrivilegedExceptionAction<T> action) throws SecurityException, PrivilegedActionException {
        final Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        return Subject.doAsPrivileged(subject, action, null);
    }
}
