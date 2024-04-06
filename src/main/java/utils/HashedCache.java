package utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class HashedCache<K, V> extends LinkedHashMap<K, CacheEntry<V>> {
    private int maxSize;

    @Override
    protected boolean removeEldestEntry(Map.Entry entry) {
        return size() > maxSize;
    }

    public HashedCache(int maxSize) {
        super(maxSize, 0.7F, true);
        this.maxSize = maxSize;
    }

    public V getItem(K key) {
        CacheEntry<V> cacheEntry = super.get(key);
        if(cacheEntry != null) {
            V value = cacheEntry.validateAndGet();
            if(value != null) {
                return value;
            } else {
                super.remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    public V putItem(K key, V value, long timeout) {
        super.put(key, new CacheEntry<>(value, timeout));
        return value;
    }
}