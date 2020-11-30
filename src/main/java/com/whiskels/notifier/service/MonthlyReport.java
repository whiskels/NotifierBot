package com.whiskels.notifier.service;

import java.util.function.Predicate;

import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;

public interface MonthlyReport<T> {
    default String monthlyReport() {
        return monthlyReport(alwaysTruePredicate());
    }

    String monthlyReport(Predicate<T> predicate);
}
