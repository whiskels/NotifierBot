package com.whiskels.notifier.common.util;

import com.whiskels.notifier.external.DataProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_NEW_LINE;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {
    public static <T> Predicate<T> alwaysTruePredicate() {
        return x -> true;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> List<T> filterAndSort(List<T> list, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .sorted()
                .collect(toList());
    }

    public static <T extends Comparable<T>> List<T> filterAndSort(List<T> list, List<Predicate<T>> predicates) {
        return list.stream()
                .filter(predicates.stream().reduce(x -> true, Predicate::and))
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

    public static <T> List<T> filterAndSort(List<T> list, Comparator<T> comparator, List<Predicate<T>> predicates) {
        return list.stream()
                .filter(predicates.stream().reduce(x -> true, Predicate::and))
                .sorted(comparator)
                .collect(toList());
    }

    @SafeVarargs
    public static <T extends Comparable<T>> List<T> filterAndSort(DataProvider<T> provider, Predicate<T>... predicates) {
        return filterAndSort(provider.get(), predicates);
    }

    @SafeVarargs
    public static <T> List<T> filterAndSort(DataProvider<T> provider, Comparator<T> comparator, Predicate<T>... predicates) {
        return filterAndSort(provider.get(), comparator, predicates);
    }

    @SafeVarargs
    public static <T> List<T> filter(List<T> list, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .collect(toList());
    }


    public static <T,R> List<R> map(List<T> list, Function<? super T, ? extends R> func) {
        return list.stream().map(func).collect(toList());
    }

    public static <T> String collectToString(Collection<T> collection, Function<T, String> toString, Collector<CharSequence, ?, String> collector) {
        return collection.stream()
                .map(toString)
                .collect(collector);
    }

    public static <T> String collectToBulletListString(Collection<T> collection, Function<T, String> toStringFunc) {
        return collection.stream()
                .map(o -> "• " + toStringFunc.apply(o))
                .collect(COLLECTOR_NEW_LINE);

    }
}
