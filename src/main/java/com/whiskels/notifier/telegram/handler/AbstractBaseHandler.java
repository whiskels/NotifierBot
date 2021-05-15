package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.events.SendMessageCreationEvent;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Abstract class for all handlers
 * <p>
 * Inheritors are marked with {@link com.whiskels.notifier.telegram.annotation.BotCommand} annotation to define
 * supported command.
 * <p>
 * Scheduling of {@link #handle(User, String)} call is possible with
 * {@link com.whiskels.notifier.telegram.annotation.Schedulable} annotation
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBaseHandler {
    @Value("${telegram.bot.admin}")
    protected String botAdmin;

    protected final AuthorizationService authorizationService;
    protected final ApplicationEventPublisher publisher;

    public final void publish(SendMessage message) {
        this.publisher.publishEvent(new SendMessageCreationEvent(message));
    }

    /**
     * Performs authorization of user and handling of the command
     *
     * @param user    {@link} User that sent update to the bot
     * @param message {@link} content of the update
     */
    public final void authorizeAndHandle(User user, String message) {
        if (this.authorizationService.authorize(this.getClass(), user)) {
            handle(user, message);
        } else {
            handleUnauthorized(user, message);
        }
    }

    /**
     * Handling of the command if user is authorized
     */
    protected abstract void handle(User user, String message);

    /**
     * Handling of the command if user is unauthorized
     */
    private void handleUnauthorized(User user, String message) {
        log.info("Unauthorized access: {} {}", user, message);
        String userChatId = String.valueOf(user.getChatId());
        publish(MessageBuilder.builder(userChatId)
                .line("Your token is *%s*", userChatId)
                .line("Please contact your supervisor to gain access")
                .build());
        publish(MessageBuilder.builder(botAdmin)
                .line("*Unauthorized access:* %s", userChatId)
                .line("*Message:* %s", message == null || message.isEmpty()
                        ? "Empty"
                        : message.replaceAll("_", "-"))
                .build());
    }
}
