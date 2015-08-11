// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentReaderHashMap<K, V> extends ConcurrentHashMap<K, V>
{
    private static final float _DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int _DEFAULT_INITIAL_CAPACITY = 16;
    private static final int _SINGLE_WRITE = 1;
    
    public ConcurrentReaderHashMap() {
        this(16);
    }
    
    public ConcurrentReaderHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public ConcurrentReaderHashMap(final int intitialCapacity, final float loadFactor) {
        super(intitialCapacity, loadFactor, 1);
    }
}
