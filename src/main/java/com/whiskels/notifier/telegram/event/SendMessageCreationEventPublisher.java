package com.whiskels.notifier.telegram.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.Nullable;

@Component
@RequiredArgsConstructor
public class SendMessageCreationEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(@Nullable SendMessage message) {
        publisher.publishEvent(new SendMessageCreationEvent(message));
    }
}
