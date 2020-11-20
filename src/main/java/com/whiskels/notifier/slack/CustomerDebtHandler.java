package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import com.whiskels.notifier.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.slack.api.webhook.WebhookPayloads.payload;
import static com.whiskels.notifier.service.CustomerService.alwaysTruePredicate;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerDebtHandler {
    @Value("${slack.customer.webhook}")
    private String webHook;

    private final CustomerService customerService;

    @Scheduled(cron = "${slack.customer.cron}")
    public void createAndSendPayload() {
        try {
            final Slack slack = Slack.getInstance();
            WebhookResponse response = slack.send(webHook, payload(p -> p.text(customerService.createCustomerDebtMessage(alwaysTruePredicate()))));
            log.info(response.toString());
        } catch (IOException e) {
            log.error("Error when sending customer info to Slack: {}", e.toString());
        }
    }
}
