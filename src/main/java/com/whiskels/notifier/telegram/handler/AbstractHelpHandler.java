package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.function.Function;

import static com.whiskels.notifier.telegram.Command.ADMIN_HELP;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractCommand;

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
        MessageBuilder builder = MessageBuilder.builder(user);
        generateMessage(builder, message);
        generateButtons(builder, user);
        publish(builder.build());
    }

    private void generateMessage(MessageBuilder builder, String message) {
        if (extractCommand(message).equals(ADMIN_HELP.name())) {
            builder.line("Help message");
        } else {
            builder.line("Hello. I'm *%s*", botName)
                    .line("Here are your available commands");
        }
    }

    private void generateButtons(MessageBuilder builder, User user) {
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
