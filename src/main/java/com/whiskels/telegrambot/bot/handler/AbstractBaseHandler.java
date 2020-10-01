package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.security.AuthorizationService;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

import static com.whiskels.telegrambot.util.TelegramUtil.createMessageTemplate;

/**
 * Abstract class for all handlers
 */
@Slf4j
public abstract class AbstractBaseHandler {
    @Value("${bot.admin}")
    protected String botAdmin;

    @Autowired
    protected AuthorizationService authorizationService;

    public final List<PartialBotApiMethod<? extends Serializable>> authorizeAndHandle(User user, String message) {
        return this.authorizationService.authorize(this.getClass(), user) ?
                handle(user, message) : handleUnauthorized(user, message);
    }

    protected abstract List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    private List<PartialBotApiMethod<? extends Serializable>> handleUnauthorized(User user, String message) {
        log.info("Unauthorized access: {} {}", user, message);
        return List.of(
                createMessageTemplate(user).setText(String.format(
                        "Your token is *%s*%nPlease contact your supervisor to gain access",
                        user.getChatId())),
                createMessageTemplate(botAdmin).setText(String.format(
                        "Unauthorized access *%s*%n%s", user, message))
        );
    }
}
