package com.whiskels.notifier.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {

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
