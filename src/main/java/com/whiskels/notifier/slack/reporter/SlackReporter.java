package com.whiskels.notifier.slack.reporter;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.slack.SlackPayload;
import com.whiskels.notifier.slack.SlackPayloadCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@AllArgsConstructor
public abstract class SlackReporter<T> {
    protected final String webHook;
    protected final ApplicationEventPublisher publisher;
    protected final DataProvider<T> provider;

    public abstract void report();

    protected final void publish(SlackPayload payload) {
        log.debug("SlackPayload publishing triggered");
        this.publisher.publishEvent(new SlackPayloadCreatedEvent(payload));
    }
}