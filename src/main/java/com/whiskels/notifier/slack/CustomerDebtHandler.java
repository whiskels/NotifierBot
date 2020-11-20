package com.whiskels.notifier.slack;

import com.whiskels.notifier.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDebtHandler extends AbstractSlackHandler {
    @Value("${slack.customer.webhook}")
    private String webHook;

    private final CustomerService customerService;

    @Scheduled(cron = "${slack.customer.cron}")
    public void dailyPayload() {
        createAndSendPayload(webHook, customerService.createCustomerDebtMessage(CustomerService.alwaysTruePredicate()));
    }
}
