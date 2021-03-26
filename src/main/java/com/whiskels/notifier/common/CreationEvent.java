package com.whiskels.notifier.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreationEvent<T> {
    private final T object;

    public T get() {
        return object;
    }
}
