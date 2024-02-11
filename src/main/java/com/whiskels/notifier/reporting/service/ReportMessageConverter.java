package com.whiskels.notifier.reporting.service;

import com.slack.api.webhook.Payload;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ReportMessageConverter<T> {
    @Nonnull
    Iterable<Payload> convert(@Nonnull final ReportData<T> data);
}
