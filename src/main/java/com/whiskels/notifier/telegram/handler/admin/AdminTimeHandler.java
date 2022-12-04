package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Clock;

import static com.whiskels.notifier.common.util.DateTimeUtil.format;
import static com.whiskels.notifier.telegram.Command.ADMIN_TIME;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
class AdminTimeHandler implements CommandHandler {
    private final Clock clock;

    @Override
    @Secured(ADMIN)
    public SendMessage handle(User admin, String message) {
        return builder(admin)
                .line("*Bot current time*:")
                .line(format(now(clock)))
                .line("*Server time*:")
                .line(format(now()))
                .build();
    }

    @Override
    public Command getCommand() {
        return ADMIN_TIME;
    }
}
