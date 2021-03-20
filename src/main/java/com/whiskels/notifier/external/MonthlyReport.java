package com.whiskels.notifier.external;

import java.util.function.Predicate;

import static com.whiskels.notifier.common.StreamUtil.alwaysTruePredicate;

public interface MonthlyReport<T> {
    default String monthlyReport() {
        return monthlyReport(alwaysTruePredicate());
    }

    String monthlyReport(Predicate<T> predicate);
}
