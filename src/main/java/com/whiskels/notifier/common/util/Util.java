package com.whiskels.notifier.common.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class Util {
    public static <T> Predicate<T> alwaysTruePredicate() {
        return x -> true;
    }

    public static <T, R> Predicate<T> notNull(Function<T, R> func) {
        return e -> func.apply(e) != null;
    }

    public static <T> T defaultIfNull(T val, T defaultVal) {
        return val != null ? val : defaultVal;
    }
}
