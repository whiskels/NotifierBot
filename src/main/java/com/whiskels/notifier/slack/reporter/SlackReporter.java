package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.slack.SlackWebHookExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class SlackReporter<T> {
    protected final String webHook;
    protected final SlackWebHookExecutor executor;
    protected final ReportSupplier<T> provider;

    public abstract void prepareAndSend();
}