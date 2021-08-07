package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractUserHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static com.whiskels.notifier.telegram.Command.ADMIN_PROMOTE;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;

/**
 * Allows bot admin to change user roles by sending bot a chat command
 * Syntax: /ADMIN_PROMOTE userId role
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = ADMIN_PROMOTE, requiredRoles = {ADMIN})
public class AdminPromoteHandler extends AbstractUserHandler {
    public AdminPromoteHandler(AuthorizationService authorizationService,
                               ApplicationEventPublisher publisher,
                               UserService userService) {
        super(authorizationService, publisher, userService);
    }

    @Override
    protected void handle(User admin, String message) {
        final String[] arguments = extractArguments(message).split(" ");
        final int userId = Integer.parseInt(arguments[0]);

        final User toUpdate = userService.get(userId).orElse(null);
        String roleValue = "";

        if (toUpdate != null) {
            try {
                roleValue = arguments[1].trim();
                final Role roleToUpdate = Role.valueOf(roleValue);
                Set<Role> userRoles = toUpdate.getRoles();

                if (toUpdate.getRoles().contains(roleToUpdate)) {
                    userRoles = userRoles.stream()
                            .filter(role -> role != roleToUpdate)
                            .collect(Collectors.toSet());
                    if (userRoles.isEmpty()) {
                        userRoles.add(UNAUTHORIZED);
                    }
                } else {
                    userRoles.add(roleToUpdate);
                    if (userRoles.size() > 1) {
                        userRoles.remove(UNAUTHORIZED);
                    }
                }


                toUpdate.setRoles(userRoles);
                userService.update(toUpdate);

                publish(builder(admin)
                        .line("Updated user: %s", toUpdate.toString())
                        .build());
                publish(builder(toUpdate)
                        .line("Your roles were updated: %s", userRoles.stream()
                                .map(Role::toString)
                                .collect(Collectors.joining(", ")))
                        .build());
            } catch (IllegalArgumentException e) {
                publish(builder(admin)
                        .line("Illegal role: %s", roleValue)
                        .build());
            }
        } else {
            publish(builder(admin)
                    .line("Couldn't find user: %d", userId)
                    .build());
        }
    }
}
