package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractUserHandler;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.ParsingUtil.extractArguments;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Notifies all bot users with a message from admin
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = "/ADMIN_MESSAGE", requiredRoles = {ADMIN})
public class AdminMessageHandler extends AbstractUserHandler {
    @Override
    protected void handle(User admin, String text) {
        log.debug("Preparing /ADMIN_MESSAGE");
        List<SendMessage> messages = userService.getUsers()
                .stream()
                .map(user -> create(user)
                        .line(extractArguments(text))
                        .build())
                .collect(Collectors.toList());

        log.debug("Prepared {} messages", messages.size());
        messages.add(create(admin)
                .line("Notified %d users", messages.size())
                .build());

        messages.forEach(this::publish);
    }
}
