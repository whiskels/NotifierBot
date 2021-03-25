package com.whiskels.notifier.slack.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.slack.SlackWebHookHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("slack-common")
@Slf4j
@ConditionalOnProperty("slack.customer.payment.webhook")
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DailyReporter.class)
public class CustomerPaymentWebhookHandler implements SlackWebHookHandler {
    @Value("${slack.customer.payment.webhook}")
    private String webHook;

    private final DailyReporter<ReceivableDto> reporter;

    @Scheduled(cron = "${slack.customer.payment.cron}", zone = "${common.timezone}")
    public void dailyPayload() {
        final String result = sendDailyReport(webHook, reporter);
        log.info(result);
    }
}