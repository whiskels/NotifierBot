package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
public abstract class AbstractSlackHandler {
    protected void createAndSendPayload(String webHook, String message) {
        try {
            final Slack slack = Slack.getInstance();
            WebhookResponse response = slack.send(webHook, payload(p -> p.text(message)));
            log.info(response.toString());
        } catch (IOException e) {
            log.error("Error when sending message {} to Slack: {}", message, e.toString());
        }
    }
}
