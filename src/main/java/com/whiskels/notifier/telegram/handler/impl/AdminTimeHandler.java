package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.format;
import static com.whiskels.notifier.telegram.Command.ADMIN_TIME;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static java.time.LocalDateTime.now;

/**
 * Shows current time on bot's server
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = ADMIN_TIME, requiredRoles = {ADMIN})
@RequiredArgsConstructor
public class AdminTimeHandler extends AbstractBaseHandler {
    private final Clock clock;

    @Override
    protected void handle(User admin, String message) {
        publish(builder(admin)
                .line("*Bot current time*:")
                .line(format(now(clock)))
                .line("*Server time*:")
                .line(format(now()))
                .build());
    }
}
