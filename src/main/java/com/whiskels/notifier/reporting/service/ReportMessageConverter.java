package com.whiskels.notifier.reporting.service;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ReportMessageConverter<T> {
    @Nonnull
    Iterable<Report> convert(@Nonnull final ReportData<T> data);
}
