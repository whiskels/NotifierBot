package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.command.AbstractBaseHandler;
import com.whiskels.telegrambot.bot.command.BotCommand;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class UpdateHandler {
    private final List<AbstractBaseHandler> handlers;

    public UpdateHandler(UserService userService, List<AbstractBaseHandler> handlers) {
        this.handlers = handlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        if (isMessageWithText(update)) {
            final Message message = update.getMessage();

            return handle(message.getFrom().getId(), message.getText());
        } else if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();

            return handle(callbackQuery.getFrom().getId(), callbackQuery.getData());
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> handle(Integer userId, String text) {
        AbstractBaseHandler handler = handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(BotCommand.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(BotCommand.class)
                        .command())
                        .anyMatch(c -> c.equalsIgnoreCase(extractCommand(text))))
                .findAny()
                .orElse(null);

        if (handler != null) {
            return handler.authenticateAndHandle(userId, text);
        }

        return Collections.emptyList();
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    private String extractCommand(String text) {
        return text.split(" ")[0];
    }
}
