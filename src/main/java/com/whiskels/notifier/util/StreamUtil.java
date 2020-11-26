package com.whiskels.notifier.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtil {
    private StreamUtil() {
    }

    @SafeVarargs
    public static <T> List<T> filterAndSort(List<T> list, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .sorted()
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> alwaysTruePredicate() {
        return x -> true;
    }
}
