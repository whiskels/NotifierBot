package com.whiskels.notifier.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class CacheService<T> {
    private final int size;

    @Getter
    private final List<List<T>> cachedData;

    public CacheService(int size) {
        this.size = size;
        cachedData = new ArrayList<>(size);
        IntStream.of(0, size - 1).forEach(i -> cachedData.add(i, new ArrayList<>()));
    }

    public void updateCache(List<T> newEntry) {
        for (int i = size - 1; i >= 0; i--) {
            if (i != 0) {
                cachedData.set(i, cachedData.get(i - 1));
            } else {
                cachedData.set(i, newEntry);
            }
        }
    }

    public Predicate<T> notCached() {
        return c -> cachedData.stream().noneMatch(data -> data.contains(c));
    }
}
