package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractUserHandler;
import com.whiskels.notifier.telegram.service.UserService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.common.ParsingUtil.extractArguments;

/**
 * Allows bot admin to change user name by sending bot a chat command
 * Syntax: /ADMIN_NAME userId name
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/ADMIN_NAME", requiredRoles = {ADMIN})
@Profile("telegram-common")
public class AdminUpdateNameHandler extends AbstractUserHandler {

    public AdminUpdateNameHandler(UserService userService) {
        super(userService);
    }

    @Override
    protected List<BotApiMethod<Message>> handle(User admin, String message) {
        log.debug("Preparing /ADMIN_NAME");
        final String arguments = extractArguments(message);
        final int userId = Integer.parseInt(arguments.substring(0, arguments.indexOf(" ")));

        final User toUpdate = userService.get(userId).orElse(null);

        if (toUpdate != null) {
            toUpdate.setName(extractArguments(arguments));
            userService.update(toUpdate);

            return List.of(MessageBuilder.create(admin)
                    .line("Updated user: %s", toUpdate.toString())
                    .build());
        } else {
            return List.of(MessageBuilder.create(admin)
                    .line("Couldn't find user: %d", userId)
                    .build());
        }
    }
}
