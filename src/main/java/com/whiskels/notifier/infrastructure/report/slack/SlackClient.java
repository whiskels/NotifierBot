package com.whiskels.notifier.infrastructure.report.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class SlackClient {
    private final Slack slack;

    public void send(String webhook, Payload payload) throws IOException {
        var response = slack.send(webhook, payload);
        if (response.getCode() != HttpStatus.OK.value()) {
            throw new RuntimeException(STR."Error on slack call: \{response.getCode()}: \{response.getMessage()}");
        }
    }
}
