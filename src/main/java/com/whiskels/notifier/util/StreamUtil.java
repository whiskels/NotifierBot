package com.whiskels.notifier.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class StreamUtil {
    private StreamUtil() {
    }

    public static <T> List<T> filterAndSort(List<T> list, List<Predicate<T>> predicates) {
        return list.stream()
                .filter(predicates.stream().reduce(x -> true, Predicate::and))
                .sorted()
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> alwaysTruePredicate() {
        return x -> true;
    }
}
