package com.whiskels.notifier.common;

import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

@AllArgsConstructor
public class CreationEvent<T> {
    private final T object;

    @Nullable
    public T get() {
        return object;
    }
}
