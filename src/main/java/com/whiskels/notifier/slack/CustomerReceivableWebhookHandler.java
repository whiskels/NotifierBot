package com.whiskels.notifier.slack;

import com.whiskels.notifier.service.CustomerReceivableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"slack", "slack-test"})
public class CustomerReceivableWebhookHandler extends AbstractSlackWebHookHandler {
    @Value("${slack.customer.receivable.webhook}")
    private String webHook;

    private final CustomerReceivableService customerReceivableService;

    @Scheduled(cron = "${slack.customer.receivable.cron}")
    public void dailyPayload() {
        createAndSendPayload(webHook, customerReceivableService.dailyMessage());
    }
}
