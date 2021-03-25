package com.whiskels.notifier.external;

import java.util.function.Predicate;

import static com.whiskels.notifier.common.StreamUtil.alwaysTruePredicate;

public interface DailyReporter<T> {
    default String dailyReport() {
        return dailyReport(alwaysTruePredicate());
    }

    String dailyReport(Predicate<T> predicate);
}
