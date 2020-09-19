package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.handler.*;
import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.*;

@Component
@Slf4j
public class UpdateReceiver {
    private UserService userService;
    private List<AbstractHandler> handlers;

    public UpdateReceiver(UserService userService, List<AbstractHandler> handlers) {
        this.userService = userService;
        this.handlers = handlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            AbstractHandler handlerForCommand = handlers.stream()
                    .filter(handler -> getCommand(update.getMessage())
                            .equals(handler.supportedCommand()))
                    .findAny()
                    .orElse(null);
            if (handlerForCommand != null) {
                final Message message = update.getMessage();
                final String chatId = message.getFrom().getId().toString();
                return handlerForCommand.operate(chatId, message);
            }
        }
        return null;
    }

    private Command getCommand(Message message) {
        final User user = getUser(message);
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
            }
        }
        return NONE;
    }

    private User getUser(Message message) {
        final String chatId = message.getFrom().getId().toString();
        return userService.containsUser(chatId) ? userService.addUser(new User(chatId)) : userService.getUser(chatId);
    }


}
