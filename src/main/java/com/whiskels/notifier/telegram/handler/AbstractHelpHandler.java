package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.function.Function;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Shows help message and dynamically created inline keyboard based on user role
 * <p>
 * Available to: everyone
 */
@RequiredArgsConstructor
public abstract class AbstractHelpHandler extends AbstractBaseHandler {
    private final Function<Command, String> descriptionFunc;

    @Value("${telegram.bot.name:TelegramNotifierBot}")
    private String botName;

    @Autowired
    private List<AbstractBaseHandler> handlers;

    @Override
    protected void handle(User user, String message) {
        publish(generateMessage(user));
    }

    private SendMessage generateMessage(User user) {
        MessageBuilder builder = getBuilder(user);
        generateButtons(user, builder);
        return builder.build();
    }

    private MessageBuilder getBuilder(User user) {
        if (user.getRoles().contains(ADMIN)) {
            return MessageBuilder.builder(user).line("Help message");
        }

        return MessageBuilder.builder(user)
                .line("Hello. I'm *%s*", botName)
                .line("Here are your available commands");
    }

    private void generateButtons(User user, MessageBuilder builder) {
        // Dynamically creates buttons if handler has message and is available to user
        for (AbstractBaseHandler handler : handlers) {
            BotCommand annotation = handler.getClass().getAnnotation(BotCommand.class);
            Command command = annotation.command()[0];
            String description = descriptionFunc.apply(command);
            if (!description.isEmpty() && authorizationService.authorize(handler.getClass(), user)) {
                builder.row()
                        .button(description, command);
            }
        }
    }
}
