package com.whiskels.notifier.reporting.service;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.slack.builder.SlackPayloadBuilder;

public record SimpleReport(String header, String body) {
    public Payload toSlackPayload() {
        return SlackPayloadBuilder.builder()
                .header(header)
                .notifyChannel()
                .block(body)
                .build();
    }
}
