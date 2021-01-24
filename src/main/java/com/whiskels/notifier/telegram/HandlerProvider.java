package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static com.whiskels.notifier.util.ParsingUtil.extractCommand;

/**
 * Chooses suitable inheritor of AbstractBaseHandler to handle the input
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"telegram", "telegram-test"})
public class HandlerProvider {
    @Getter
    private final List<AbstractBaseHandler> handlers;

    /**
     * Selects handler which can handle received command
     *
     * @param text content of received message
     * @return handler suitable for command
     */
    public AbstractBaseHandler getHandler(String text) {
        return handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(BotCommand.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(BotCommand.class)
                        .command())
                        .anyMatch(c -> c.equalsIgnoreCase(extractCommand(text))))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
