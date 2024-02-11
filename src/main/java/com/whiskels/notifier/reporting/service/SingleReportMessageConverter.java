package com.whiskels.notifier.reporting.service;

import com.slack.api.webhook.Payload;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface SingleReportMessageConverter<T> {
    @Nullable
    Payload convert(@Nonnull final ReportData<T> data);
}
