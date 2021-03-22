package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Shows current time on bot's server
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/ADMIN_TIME", message = "Show bot server time", requiredRoles = {ADMIN})
@Profile("telegram-common")
@RequiredArgsConstructor
public class AdminTimeHandler extends AbstractBaseHandler {
    private final Clock clock;

    @Override
    public List<BotApiMethod<Message>> handle(User admin, String message) {
        log.debug("Preparing /ADMIN_TIME");
        return List.of(MessageBuilder.create(admin)
                .line("*Bot current time*:")
                .line(LocalDateTime.now(clock).toString())
                .build());
    }
}
