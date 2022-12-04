package com.whiskels.notifier.external;

@FunctionalInterface
public interface ReportSupplier<T> {
    ReportData<T> get();
}
