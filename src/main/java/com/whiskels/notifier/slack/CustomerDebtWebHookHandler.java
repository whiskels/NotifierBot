package com.whiskels.notifier.slack;

import com.whiskels.notifier.service.CustomerDebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"slack", "slack-test"})
public class CustomerDebtWebHookHandler extends AbstractSlackWebHookHandler {
    @Value("${slack.customer.debt.webhook}")
    private String webHook;

    private final CustomerDebtService customerDebtService;

    @Scheduled(cron = "${slack.customer.debt.cron}")
    public void dailyPayload() {
        createAndSendPayload(webHook, customerDebtService.dailyMessage());
    }
}
