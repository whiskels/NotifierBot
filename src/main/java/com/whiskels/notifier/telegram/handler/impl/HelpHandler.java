package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.Command.START;

/**
 * Shows help message and dynamically created inline keyboard based on user role
 * <p>
 * Available to: everyone
 */
@Slf4j
@BotCommand(command = {HELP, START})
public class HelpHandler extends AbstractBaseHandler {
    @Value("${telegram.bot.name:TelegramNotifierBot}")
    private String botUsername;
    private final List<AbstractBaseHandler> handlers;

    public HelpHandler(AuthorizationService authorizationService,
                       ApplicationEventPublisher publisher,
                       List<AbstractBaseHandler> handlers) {
        super(authorizationService, publisher);
        this.handlers = handlers;
    }

    @Override
    protected void handle(User user, String message) {
        MessageBuilder builder = MessageBuilder.builder(user)
                .line("Hello. I'm *%s*", botUsername)
                .line("Here are your available commands")
                .line("Use [/help] command to display this message");

        // Dynamically creates buttons if handler has message and is available to user
        for (AbstractBaseHandler handler : handlers) {
            BotCommand annotation = handler.getClass().getAnnotation(BotCommand.class);
            Command command = annotation.command()[0];
            String description = annotation.command()[0].getDescription();
            if (!description.isEmpty() && authorizationService.authorize(handler.getClass(), user)) {
                builder.row()
                        .button(description, command);
            }
        }

        publish(builder.build());
    }
}
