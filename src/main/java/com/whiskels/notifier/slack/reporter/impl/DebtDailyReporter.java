package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_TWO_NEW_LINES;

@Component
@Profile("slack-common")
@Slf4j
@ConditionalOnProperty("slack.customer.debt.webhook")
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DataProvider.class)
public class DebtDailyReporter extends SlackReporter<Debt> {
    private static final String NAME = "Debt";

    public DebtDailyReporter(@Value("${slack.customer.debt.webhook}") String webHook,
                             DataProvider<Debt> provider,
                             ApplicationEventPublisher publisher,
                             Clock clock) {
        super(webHook, provider, clock, publisher);
    }

    @Scheduled(cron = "${slack.customer.debt.cron}", zone = "${common.timezone}")
    protected void report() {
        publish(new SlackPayload(webHook, payload(NAME, COLLECTOR_TWO_NEW_LINES)));
    }
}
