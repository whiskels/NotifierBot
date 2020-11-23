package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.UserService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.whiskels.notifier.model.Role.ADMIN;
import static com.whiskels.notifier.util.TelegramUtil.extractArguments;

/**
 * Allows bot admin to change user roles by sending bot a chat command
 * Syntax: /ADMIN_PROMOTE userId role
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/ADMIN_PROMOTE")
@Profile({"telegram", "telegram-test"})
public class AdminPromoteHandler extends AbstractUserHandler {
    public AdminPromoteHandler(UserService userService) {
        super(userService);
    }

    @Override
    @RequiredRoles(roles = ADMIN)
    protected List<BotApiMethod<Message>> handle(User admin, String message) {
        log.debug("Preparing /ADMIN_PROMOTE");
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
                    userRoles.remove(roleToUpdate);
                } else {
                    userRoles.add(roleToUpdate);
                }

                toUpdate.setRoles(userRoles);
                userService.update(toUpdate);

                return List.of(
                        MessageBuilder.create(admin)
                                .line("Updated user: %s", toUpdate.toString())
                                .build(),
                        MessageBuilder.create(toUpdate)
                                .line("Your roles were updated: %s", userRoles.stream()
                                        .map(Role::toString)
                                        .collect(Collectors.joining(", ")))
                                .build());
            } catch (IllegalArgumentException e) {
                return List.of(MessageBuilder.create(admin)
                        .line("Illegal role: %s", roleValue)
                        .build());
            }
        } else {
            return List.of(MessageBuilder.create(admin)
                    .line("Couldn't find user: %d", userId)
                    .build());
        }
    }
}
