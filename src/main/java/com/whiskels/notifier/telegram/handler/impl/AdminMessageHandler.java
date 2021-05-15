package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractUserHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.telegram.Command.ADMIN_MESSAGE;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;

/**
 * Notifies all bot users with a message from admin
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = ADMIN_MESSAGE, requiredRoles = {ADMIN})
public class AdminMessageHandler extends AbstractUserHandler {
    public AdminMessageHandler(AuthorizationService authorizationService,
                               ApplicationEventPublisher publisher,
                               UserService userService) {
        super(authorizationService, publisher, userService);
    }

    @Override
    protected void handle(User admin, String text) {
        List<SendMessage> messages = userService.getUsers()
                .stream()
                .map(user -> builder(user)
                        .line(extractArguments(text))
                        .build())
                .collect(Collectors.toList());

        log.debug("Prepared {} messages", messages.size());
        messages.add(builder(admin)
                .line("Notified %d users", messages.size())
                .build());

        messages.forEach(this::publish);
    }
}
