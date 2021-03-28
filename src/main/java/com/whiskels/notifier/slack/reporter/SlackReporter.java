package com.whiskels.notifier.slack.reporter;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackPayloadCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;

@Slf4j
@AllArgsConstructor
public abstract class SlackReporter<T> {
    private final String webHook;
    private final DailyReporter<T> dailyReporter;

    @Autowired
    protected ApplicationEventPublisher publisher;

    @PostConstruct
    private void logReporter() {
        log.info("Slack reporter active: {}", this.getClass().getSimpleName());
    }

    protected void report() {
        publish(new SlackPayload(webHook, Payload.builder().text(dailyReporter.dailyReport()).build()));
    }

    protected final void publish(SlackPayload payload) {
        this.publisher.publishEvent(new SlackPayloadCreatedEvent(payload));
    }
}