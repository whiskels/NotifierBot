package com.whiskels.notifier.service;

import java.util.function.Predicate;

import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;

public interface DailyReport<T> {
    default String dailyReport() {
        return dailyReport(alwaysTruePredicate());
    }

    String dailyReport(Predicate<T> predicate);
}
