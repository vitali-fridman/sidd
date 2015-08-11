// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.filesystem;

import com.vontu.util.ProtectError;

public class FileSystemError extends ProtectError
{
    public static final FileSystemError FILE_COPY_EXCEEDED_ALLOWABLE_EXCEPTIONS;
    public static final FileSystemError FILE_COPY_CANT_ALLOCATE_BUFFER;
    public static final FileSystemError FILE_COPY_CANT_WRITE;
    public static final FileSystemError FILE_COPY_FILE_NOT_FOUND;
    public static final FileSystemError FILE_COPY_CANT_READ;
    
    protected FileSystemError(final int value, final String description) throws IllegalArgumentException {
        super(value, description);
    }
    
    private FileSystemError(final int value) throws IllegalArgumentException {
        super(value, "File system error");
    }
    
    static {
        FILE_COPY_EXCEEDED_ALLOWABLE_EXCEPTIONS = new FileSystemError(7101);
        FILE_COPY_CANT_ALLOCATE_BUFFER = new FileSystemError(7104);
        FILE_COPY_CANT_WRITE = new FileSystemError(7106);
        FILE_COPY_FILE_NOT_FOUND = new FileSystemError(7107);
        FILE_COPY_CANT_READ = new FileSystemError(7108);
    }
}
