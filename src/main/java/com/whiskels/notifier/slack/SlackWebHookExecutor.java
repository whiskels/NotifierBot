package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.whiskels.notifier.common.CreationEvent;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnBean(SlackReporter.class)
@Slf4j
public class SlackWebHookExecutor {
    @EventListener(classes = SlackPayloadCreatedEvent.class)
    public void send(CreationEvent<SlackPayload> event) {
        SlackPayload payload = event.get();

        try {
            final Slack slack = Slack.getInstance();
           log.info(String.valueOf(slack.send(payload.getUrl(), payload.getPayload())));
        } catch (IOException e) {
            log.error("Error while sending {} to webhook: {}", payload.getPayload(), payload.getUrl());
        }
    }
}
