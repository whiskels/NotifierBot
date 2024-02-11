package com.whiskels.notifier.utilities.collections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.whiskels.notifier.utilities.formatters.StringFormatter.COLLECTOR_NEW_LINE;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {
    @SafeVarargs
    public static <T extends Comparable<T>> List<T> filterAndSort(List<T> list, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .sorted()
                .collect(toList());
    }

    @SafeVarargs
    public static <T> List<T> filterAndSort(List<T> list, Comparator<T> comparator, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .sorted(comparator)
                .collect(toList());
    }

    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> func) {
        return list.stream().map(func).collect(toList());
    }

    public static <T> String collectToBulletListString(Collection<T> collection, Function<T, String> toStringFunc) {
        return collection.stream()
                .map(o -> STR."• \{toStringFunc.apply(o)}")
                .collect(COLLECTOR_NEW_LINE);
    }
}
