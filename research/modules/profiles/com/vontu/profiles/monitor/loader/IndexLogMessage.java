// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.monitor.loader;

import com.vontu.communication.data.IndexDescriptorMarshallable;

public class IndexLogMessage
{
    public static Object[] prependIndexDescriptorArgs(final IndexDescriptorMarshallable descriptor, final Object... args) {
        final Object[] result = new Object[args.length + 3];
        result[0] = descriptor.getIndexId();
        result[1] = descriptor.getProfile().getProfileName();
        result[2] = descriptor.getIndexVersion();
        System.arraycopy(args, 0, result, 3, args.length);
        return result;
    }
}
