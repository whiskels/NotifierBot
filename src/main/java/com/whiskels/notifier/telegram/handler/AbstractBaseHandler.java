package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Abstract class for all handlers
 * <p>
 * Inheritors are marked with {@link com.whiskels.notifier.telegram.annotations.BotCommand} annotation to define
 * supported command.
 * <p>
 * Call to {@link #handle(User, String)} can be secured with
 * {@link com.whiskels.notifier.telegram.annotations.RequiredRoles} annotation
 * <p>
 * Scheduling of {@link #handle(User, String)} call is possible with
 * {@link com.whiskels.notifier.telegram.annotations.Schedulable} annotation
 */
@Slf4j
public abstract class AbstractBaseHandler {
    @Value("${telegram.bot.admin}")
    protected String botAdmin;

    @Autowired
    protected AuthorizationService authorizationService;

    /**
     * Performs authorization of user and handling of the command
     *
     * @param user {@link} User that sent update to the bot
     * @param message {@link} content of the update
     * @return List of {@link BotApiMethod<Message>} that bot will send
     */
    public final List<BotApiMethod<Message>> authorizeAndHandle(User user, String message) {
        return this.authorizationService.authorize(this.getClass(), user) ?
                handle(user, message) : handleUnauthorized(user, message);
    }

    /**
     * Handling of the command if user is authorized
     */
    protected abstract List<BotApiMethod<Message>> handle(User user, String message);

    /**
     * Handling of the command if user is unauthorized
     */
    private List<BotApiMethod<Message>> handleUnauthorized(User user, String message) {
        log.info("Unauthorized access: {} {}", user, message);
        String userChatId = String.valueOf(user.getChatId());
        return List.of(MessageBuilder.create(userChatId)
                        .line("Your token is *%s*", userChatId)
                        .line("Please contact your supervisor to gain access")
                        .build(),
                MessageBuilder.create(botAdmin)
                        .line("*Unauthorized access:* %s", userChatId)
                        .line("*Message:* %s", message.replaceAll("_","-"))
                        .build());
    }
}
