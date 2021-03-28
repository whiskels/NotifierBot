package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("slack-common")
@Slf4j
@ConditionalOnProperty("slack.customer.debt.webhook")
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DailyReporter.class)
public class DebtDailyReporter extends SlackReporter<Debt> {
    public DebtDailyReporter(@Value("${slack.customer.debt.webhook}") String webHook,
                             DailyReporter<Debt> dailyReporter,
                             ApplicationEventPublisher publisher) {
        super(webHook, dailyReporter, publisher);
    }

    @Scheduled(cron = "${slack.customer.debt.cron}", zone = "${common.timezone}")
    public void scheduledReport() {
        report();
    }
}
