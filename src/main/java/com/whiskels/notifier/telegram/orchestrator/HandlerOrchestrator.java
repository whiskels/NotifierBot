package com.whiskels.notifier.telegram.orchestrator;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static com.whiskels.notifier.telegram.util.ParsingUtil.extractCommand;

/**
 * Chooses suitable inheritor of AbstractBaseHandler to handle the input
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("telegram-common")
public class HandlerOrchestrator {
    private final List<AbstractBaseHandler> handlers;
    private final UserService userService;

    public void operate(int userId, String text) {
        try {
            AbstractBaseHandler handler = getHandler(text);
            log.debug("Found handler {} for command {}", handler.getClass().getSimpleName(), text);
            handler.authorizeAndHandle(userService.getOrCreate(userId), text);
        } catch (UnsupportedOperationException e) {
            log.error("Command: {} is unsupported", text);
        }
    }

    /**
     * Selects handler which can handle received command
     *
     * @param text content of received message
     * @return handler suitable for command
     */
    protected AbstractBaseHandler getHandler(String text) {
        return handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(BotCommand.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(BotCommand.class)
                        .command())
                        .anyMatch(c -> c.toString().equalsIgnoreCase(extractCommand(text))))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
