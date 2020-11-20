package com.whiskels.notifier.slack;

import com.whiskels.notifier.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"slack", "test"})
public class CustomerDebtWebHookWebHookHandler extends AbstractSlackWebHookHandler {
    @Value("${slack.customer.webhook}")
    private String webHook;

    private final CustomerService customerService;

    @Scheduled(cron = "${slack.customer.cron}")
    public void dailyPayload() {
        createAndSendPayload(webHook, customerService.createCustomerDebtMessage(CustomerService.alwaysTruePredicate()));
    }
}
