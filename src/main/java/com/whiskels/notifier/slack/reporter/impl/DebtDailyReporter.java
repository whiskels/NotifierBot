package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_TWO_NEW_LINES;
import static com.whiskels.notifier.slack.reporter.builder.SlackPayloadBuilder.builder;

@Component
@ConditionalOnProperty("slack.customer.debt.webhook")
@ConditionalOnBean(value = Debt.class, parameterizedContainer = DataProvider.class)
public class DebtDailyReporter extends SlackReporter<Debt> {
    @Value("${slack.customer.debt.header:Debt report on}")
    private String header;

    public DebtDailyReporter(@Value("${slack.customer.debt.webhook}") String webHook,
                             DataProvider<Debt> provider,
                             ApplicationEventPublisher publisher) {
        super(webHook, publisher, provider);
    }

    @Scheduled(cron = "${slack.customer.debt.cron:0 0 13 * * MON-FRI}", zone = "${common.timezone}")
    public void report() {
        publish(builder()
                .hook(webHook)
                .collector(COLLECTOR_TWO_NEW_LINES)
                .header(header + reportDate(provider.lastUpdate()))
                .notifyChannel()
                .block(provider.get())
                .build());
    }
}
