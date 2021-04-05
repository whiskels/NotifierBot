package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackPayloadCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;

@Slf4j
@AllArgsConstructor
public abstract class SlackReporter<T> {
    protected final String webHook;
    protected final ApplicationEventPublisher publisher;
    protected final DataProvider<T> provider;

    @PostConstruct
    private void logReporter() {
        log.info("Slack reporter active: {}", this.getClass().getSimpleName());
    }

    public abstract void report();

    protected final void publish(SlackPayload payload) {
        this.publisher.publishEvent(new SlackPayloadCreatedEvent(payload));
    }
}