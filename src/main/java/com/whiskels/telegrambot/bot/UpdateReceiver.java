package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.command.AbstractBaseHandler;
import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.AbstractBaseEntity;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.UserService;
import com.whiskels.telegrambot.util.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Call;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

        if (!update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText()) {
            final Message message = update.getMessage();

            return handle(getUser(message.getFrom().getId()), message.getText());
        } else if (update.hasCallbackQuery()) {
            final CallbackQuery callbackQuery = update.getCallbackQuery();

            return handle(getUser(callbackQuery.getFrom().getId()), callbackQuery.getData());
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> handle(User user, String command) {
        AbstractBaseHandler handler = handlers.stream()
                .filter(h -> getCommand(user, command)
                        .equals(h.supportedCommand()))
                .findAny()
                .orElse(null);

        if (handler != null) {
            return handler.operate(user, command);
        }
        return Collections.emptyList();
    }

    private Command getCommand(User user, String command) {
        if (command.startsWith("/")) {
            switch (command.substring(1).split(" ")[0].toUpperCase()) {
                case "START":
                case "MENU":
                case "HELP":
                    return HELP;
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

    private User getUser(int id) {
        try {
            final User user = userService.get(id);
            log.debug("Logged user: {}", user.toString());
            return user;
        } catch (NotFoundException e) {
            log.debug("User {} not found", id);
            final User user = userService.save(new User(id));
            log.debug("Saved new user to database: {}", user.toString());
            return user;
        }
    }
}
