package com.whiskels.notifier.telegram;

import com.whiskels.notifier.service.UserService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.whiskels.notifier.util.FormatUtil.extractCommand;

/**
 * Main class used to handle incoming Updates.
 * Chooses suitable inheritor of AbstractBaseHandler to handle the input
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"telegram", "telegram-test"})
public class UpdateReceiver {
    private final List<AbstractBaseHandler> handlers;
    private final UserService userService;

    /**
     * Analyzes received update and chooses correct handler if possible
     *
     * @param update received from user
     * @return list of SendMessages to execute
     */
    public List<BotApiMethod<Message>> handle(Update update) {
        try {
            int userId = 0;
            String text = null;

            if (isMessageWithText(update)) {
                final Message message = update.getMessage();
                userId = message.getFrom().getId();
                text = message.getText();
                log.debug("Update is text message {} from {}", text, userId);
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                userId = callbackQuery.getFrom().getId();
                text = callbackQuery.getData();
                log.debug("Update is callbackquery {} from {}", text, userId);
            }

            if (text != null && userId != 0) {
                return getHandler(text).authorizeAndHandle(userService.getOrCreate(userId), text);
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            log.debug("Command: {} is unsupported", update.toString());
            return Collections.emptyList();
        }
    }

    /**
     * Selects handler which can handle received command
     *
     * @param text content of received message
     * @return handler suitable for command
     */
    private AbstractBaseHandler getHandler(String text) {
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

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
