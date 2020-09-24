package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.security.AuthorizationService;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

import static com.whiskels.telegrambot.util.TelegramUtil.createMessageTemplate;

/**
 * Abstract class for all handlers
 */
@Slf4j
public abstract class AbstractBaseHandler {
    @Autowired
    protected AuthorizationService authorizationService;

    public final List<PartialBotApiMethod<? extends Serializable>> authenticateAndHandle(User user, String message) {
        return authorizationService.authorize(user) ? handle(user, message) :
                List.of(createMessageTemplate(user).setText(
                        String.format("Your token is *%s*%nPlease contact your supervisor to gain access",
                                user.getChatId())));
    }

    protected abstract List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);
}
