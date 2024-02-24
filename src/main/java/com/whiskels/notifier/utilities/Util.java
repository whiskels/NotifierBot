package com.whiskels.notifier.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {
    public static <T> T defaultIfNull(T val, T defaultVal) {
        return val != null ? val : defaultVal;
    }
}
