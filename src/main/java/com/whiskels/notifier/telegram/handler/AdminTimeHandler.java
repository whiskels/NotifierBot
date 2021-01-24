package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;

import static com.whiskels.notifier.model.Role.ADMIN;

/**
 * Shows current time on bot's server
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/ADMIN_TIME", message = "Show bot server time", requiredRoles = {ADMIN})
@Profile({"telegram", "telegram-test"})
public class AdminTimeHandler extends AbstractBaseHandler {
    @Value("${heroku.server.hour.offset}")
    private int serverHourOffset;

    @Override
    public List<BotApiMethod<Message>> handle(User admin, String message) {
        log.debug("Preparing /ADMIN_TIME");
        return List.of(MessageBuilder.create(admin)
                .line("*Bot current time*:")
                .line(LocalDateTime.now().toString())
                .line("Server hour offset is %d", serverHourOffset)
                .build());
    }
}
