package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Shows help message and dynamically created inline keyboard based on user role
 * <p>
 * Available to: everyone
 */
@Slf4j
@BotCommand(command = {"/HELP", "/START"})
@RequiredArgsConstructor
public class HelpHandler extends AbstractBaseHandler {
    @Value("${telegram.bot.name:TelegramNotifierBot}")
    private String botUsername;
    private final List<AbstractBaseHandler> handlers;

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /HELP");
        MessageBuilder builder = MessageBuilder.create(user)
                .line("Hello. I'm *%s*", botUsername)
                .line("Here are your available commands")
                .line("Use [/help] command to display this message");

        // Dynamically creates buttons if handler has message and is available to user
        for (AbstractBaseHandler handler : handlers) {
            BotCommand command = handler.getClass().getAnnotation(BotCommand.class);
            String msg = command.message();
            if (!msg.isEmpty() && authorizationService.authorize(handler.getClass(), user)) {
                builder.row()
                        .button(msg, command.command()[0]);
            }
        }

        publish(builder.build());
    }
}
