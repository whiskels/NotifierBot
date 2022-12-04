package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static com.whiskels.notifier.telegram.util.ParsingUtil.extractCommand;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Component
@Profile("telegram-common")
class HandlerOrchestrator {
    private final Map<Command, CommandHandler> handlers;
    private final CommandHandler defaultHandler;
    private final SendMessageCreationEventPublisher publisher;

    public HandlerOrchestrator(Collection<CommandHandler> handlers, CommandHandler defaultHandler, SendMessageCreationEventPublisher publisher) {
        this.defaultHandler = defaultHandler;
        this.handlers = handlers.stream().collect(toMap(CommandHandler::getCommand, Function.identity()));
        this.publisher = publisher;
    }

    public void operate(User user, String message) {
        var handler = handlers.getOrDefault(extractCommand(message), defaultHandler);
        ofNullable(handler.handle(user, message))
                .ifPresent(publisher::publish);
    }
}
