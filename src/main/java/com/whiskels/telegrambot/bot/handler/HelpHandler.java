package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.model.Role.*;

/**
 * Shows help message and inline keyboard based on user role
 * <p>
 * Available to: everyone
 */
@Component
@Slf4j
@BotCommand(command = {"/HELP", "/START"})
public class HelpHandler extends AbstractBaseHandler {
    @Value("${bot.test.name}")
    private String botUsername;

    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /HELP");
        MessageBuilder builder = MessageBuilder.create(user)
                .text("Hello. I'm *%s*%nHere are your available commands%nUse [/help] command to display this message", botUsername);

        builder.row()
                .button("Show your token", "/TOKEN");

        if (!Collections.disjoint((user).getRoles(), List.of(MANAGER, HEAD, ADMIN))) {
            builder.row()
                    .button("Get customer overdue debts", "/GET")
                    .row()
                    .button("Show/add schedule", "/SCHEDULE")
                    .row()
                    .button("Clear schedule", "/SCHEDULE_CLEAR")
                    .button("Schedule help", "/SCHEDULE_HELP");
        }

        return List.of(builder.build());
    }
}
