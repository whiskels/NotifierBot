package com.whiskels.notifier.reporting;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.slack.SlackClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class SlackPayloadExecutor {
    private final SlackClient slackClient;
    private final Map<ReportType, String> webhookMappings;

    public void send(ReportType type, Payload payload) throws IOException {
        if (!webhookMappings.containsKey(type)) {
            throw new IllegalStateException(STR."No url found for report type=\{type}");
        }
        slackClient.send(webhookMappings.get(type), payload);
    }
}
