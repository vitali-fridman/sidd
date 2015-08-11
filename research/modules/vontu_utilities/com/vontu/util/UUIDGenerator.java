// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util;

import java.util.Locale;
import java.util.UUID;

public class UUIDGenerator
{
    public String generateRandomUUID() {
        return UUID.randomUUID().toString().toUpperCase(Locale.US);
    }
}
