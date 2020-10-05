package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Abstract class for all handlers
 */
@Slf4j
public abstract class AbstractBaseHandler {
    @Value("${bot.admin}")
    protected String botAdmin;

    @Autowired
    protected AuthorizationService authorizationService;

    public final List<BotApiMethod<Message>> authorizeAndHandle(User user, String message) {
        return this.authorizationService.authorize(this.getClass(), user) ?
                handle(user, message) : handleUnauthorized(user, message);
    }

    protected abstract List<BotApiMethod<Message>> handle(User user, String message);

    private List<BotApiMethod<Message>> handleUnauthorized(User user, String message) {
        log.info("Unauthorized access: {} {}", user, message);
        String userChatId = String.valueOf(user.getChatId());
        return List.of(MessageBuilder.create(userChatId)
                        .line("Your token is *%s*")
                        .line("Please contact your supervisor to gain access",
                                userChatId)
                        .build(),
                MessageBuilder.create(botAdmin)
                        .line("Unauthorized access *%s")
                        .line(message)
                        .build());
    }
}
