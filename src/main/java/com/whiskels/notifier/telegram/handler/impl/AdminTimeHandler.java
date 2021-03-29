package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.LocalDateTime;

import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Shows current time on bot's server
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = "/ADMIN_TIME", message = "Show bot server time", requiredRoles = {ADMIN})
public class AdminTimeHandler extends AbstractBaseHandler {
    private final Clock clock;

    public AdminTimeHandler(AuthorizationService authorizationService, ApplicationEventPublisher publisher, Clock clock) {
        super(authorizationService, publisher);
        this.clock = clock;
    }

    @Override
    protected void handle(User admin, String message) {
        log.debug("Preparing /ADMIN_TIME");
        publish(create(admin)
                .line("*Bot current time*:")
                .line(LocalDateTime.now(clock).toString().replace('T', ' '))
                .build());
    }
}
