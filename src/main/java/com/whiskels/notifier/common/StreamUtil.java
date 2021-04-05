package com.whiskels.notifier.common;

import com.whiskels.notifier.external.DataProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {
    @SafeVarargs
    public static <T> List<T> filterAndSort(List<T> list, Predicate<T>... predicates) {
        return list.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .sorted()
                .collect(toList());
    }

    public static <T> List<T> filterAndSort(DataProvider<T> provider, Predicate<T>... predicates) {
        return filterAndSort(provider.get(), predicates);
    }

    public static <T> Predicate<T> alwaysTruePredicate() {
        return x -> true;
    }

    public static <T,R> List<R> map(List<T> list, Function<? super T, ? extends R> func) {
        return list.stream().map(func).collect(toList());
    }
}
