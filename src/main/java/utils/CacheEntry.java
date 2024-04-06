package utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CacheEntry<T> {
    private LocalDateTime createdAt;

    //seconds
    private long maxAge;
    private final T item;

    public CacheEntry(T item, long validFor) {
        this.createdAt = LocalDateTime.now();
        this.maxAge = validFor;
        this.item = item;
    }

    public T validateAndGet() {
        if(this.getRemainingTime() > 0) {
            return get();
        }
        return null;
    }

    public T get() {
        return item;
    }

    public long getRemainingTime() {
        long age = ChronoUnit.SECONDS.between(createdAt, LocalDateTime.now());
        return maxAge - age > 0 ? maxAge - age : 0;
    }
}
