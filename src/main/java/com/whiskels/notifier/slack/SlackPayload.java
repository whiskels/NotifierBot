package com.whiskels.notifier.slack;

import com.slack.api.webhook.Payload;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SlackPayload {
    private final String url;
    private final Payload data;
}
