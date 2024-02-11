package com.whiskels.notifier.reporting.service;

import javax.annotation.Nonnull;

public interface DataFetchService<T> {
    @Nonnull
    ReportData<T> fetch();
}
