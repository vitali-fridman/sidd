// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.collection;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class SetMapBuilder<K, V>
{
    private final Map<K, Set<V>> _setMap;
    
    public SetMapBuilder() {
        this._setMap = new HashMap<K, Set<V>>();
    }
    
    public void addSetItem(final K key, final V item) {
        addSetItem(this._setMap, key, item);
    }
    
    public static <K, V> void addSetItem(final Map<K, Set<V>> setMap, final K key, final V item) {
        getSet(setMap, key).add(item);
    }
    
    private static <K, V> Set<V> getSet(final Map<K, Set<V>> setMap, final K key) {
        Set<V> item = setMap.get(key);
        if (item == null) {
            item = new HashSet<V>();
            setMap.put(key, item);
        }
        return item;
    }
    
    public Map<K, Set<V>> getSetMap() {
        return this._setMap;
    }
}
