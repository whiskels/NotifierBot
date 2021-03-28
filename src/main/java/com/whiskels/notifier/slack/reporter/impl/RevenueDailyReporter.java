package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("slack-common")
@Slf4j
@ConditionalOnProperty("slack.customer.payment.webhook")
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DailyReporter.class)
public class RevenueDailyReporter extends SlackReporter<ReceivableDto> {
    public RevenueDailyReporter(@Value("${slack.customer.payment.webhook}") String webHook,
                                DailyReporter<ReceivableDto> dailyReporter,
                                ApplicationEventPublisher publisher) {
        super(webHook, dailyReporter, publisher);
    }

    @Scheduled(cron = "${slack.customer.payment.cron}", zone = "${common.timezone}")
    public void scheduledReport() {
        report();
    }
}
