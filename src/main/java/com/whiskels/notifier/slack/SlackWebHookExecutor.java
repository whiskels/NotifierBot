package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnBean(SlackReporter.class)
public class SlackWebHookExecutor {
    private final Slack slack = Slack.getInstance();

    public void execute(SlackPayload payload) {
        if (payload == null) return;

        try {
            log.info(String.valueOf(slack.send(payload.getUrl(), payload.getPayload())));
        } catch (IOException e) {
            log.error("Error while sending {} to webhook: {}", payload.getPayload(), payload.getUrl());
        }
    }
}
