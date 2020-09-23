package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Slf4j
public abstract class AbstractBaseHandler {
    protected static final String END_LINE = "\n";
    protected static final String EMPTY_LINE = "---------------------------";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    protected UserService userService;

    public final List<PartialBotApiMethod<? extends Serializable>> authenticateAndHandle(Integer userId, String message) {
        final User user = userService.get(userId);

        return authenticate(user) ? handle(user, message) : List.of(createMessageTemplate(user).setText(
                String.format("Your token is *%s*%nPlease contact your supervisor to gain access", user.getChatId())));
    }

    protected abstract List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    @SneakyThrows
    protected final boolean authenticate(User user) {
        Method method = this.getClass().getDeclaredMethod("handle", User.class, String.class);
        if (method.isAnnotationPresent(RequiredRoles.class)) {
            RequiredRoles annotation = method.getAnnotation(RequiredRoles.class);
            return !Collections.disjoint(user.getRoles(), Arrays.asList(annotation.roles()));
        }
        return true;
    }


}
