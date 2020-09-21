package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.handler.*;
import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import com.whiskels.telegrambot.util.exception.NotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.*;

@Component
@Slf4j
public class UpdateReceiver {
    private final UserService userService;
    private final List<AbstractBaseHandler> handlers;

    public UpdateReceiver(UserService userService, List<AbstractBaseHandler> handlers) {
        this.userService = userService;
        this.handlers = handlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            final Message message = update.getMessage();
            final User user = getUser(message);
            AbstractBaseHandler handler = handlers.stream()
                    .filter(h -> getCommand(user, message)
                            .equals(h.supportedCommand()))
                    .findAny()
                    .orElse(null);
            if (handler != null) {
                return handler.operate(user, message);
            }
        }
        return Collections.emptyList();
    }

    private Command getCommand(User user, Message message) {
        String messageContent = message.getText();

        if (messageContent.startsWith("/")) {
            switch (messageContent.substring(1).split(" ")[0].toUpperCase()) {
                case "START":
                    return START;
                case "HELP":
                    return user.isManager() ? HELP : UNAUTHORIZED;
                case "TOKEN":
                    return TOKEN;
                case "GET":
                    return user.isManager() ? GET : UNAUTHORIZED;
                case "SCHEDULE":
                    return user.isManager() ? SCHEDULE : UNAUTHORIZED;
                case "SCHEDULE_HELP":
                    return user.isManager() ? SCHEDULE_HELP : UNAUTHORIZED;
                case "SCHEDULE_CLEAR":
                    return user.isManager() ? SCHEDULE_CLEAR : UNAUTHORIZED;
                case "SCHEDULE_GET":
                    return user.isManager() ? SCHEDULE_GET : UNAUTHORIZED;
                case "ADMIN_MESSAGE":
                    return user.isAdmin() ? ADMIN_MESSAGE : UNAUTHORIZED;
                case "ADMIN_TIME":
                    return user.isAdmin() ? ADMIN_TIME : UNAUTHORIZED;
            }
        }
        return NONE;
    }

    private User getUser(Message message) {
        final int chatId = message.getFrom().getId();
        try {
            final User user = userService.get(chatId);
            log.debug("Logged user: {}", user.toString());
            return user;
        } catch (NotFoundException e) {
            log.debug("User {} not found", chatId);
            final User user = userService.save(new User(chatId));
            log.debug("Saved new user to database: {}", user.toString());
            return user;
        }
    }
}
