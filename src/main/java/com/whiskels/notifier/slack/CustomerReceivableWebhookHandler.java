package com.whiskels.notifier.slack;

import com.whiskels.notifier.external.receivable.service.ReceivableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("slack-common")
@Slf4j
public class CustomerReceivableWebhookHandler implements SlackWebHookHandler {
    @Value("${slack.customer.receivable.webhook}")
    private String webHook;

    private final ReceivableService service;

    @Scheduled(cron = "${slack.customer.receivable.cron}")
    public void dailyPayload() {
        final String result = sendDailyReport(webHook, service);
        log.info(result);
    }
}
