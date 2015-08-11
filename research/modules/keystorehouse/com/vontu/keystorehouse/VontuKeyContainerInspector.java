// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.keystorehouse;

import java.util.Date;
import com.vontu.security.KeyStorehouseException;

public final class VontuKeyContainerInspector
{
    public static byte[] readKeyBytes(final KeyContainer container) throws KeyStorehouseException {
        return container.getHmacKey();
    }
    
    public static Date getCreationDate(final KeyStorehouse store, final String alias) throws KeyStorehouseException {
        return store.getCreationDate(alias);
    }
}
