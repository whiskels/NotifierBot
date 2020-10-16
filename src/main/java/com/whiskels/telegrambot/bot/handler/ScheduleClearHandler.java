package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.annotations.BotCommand;
import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.annotations.RequiredRoles;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.telegrambot.model.Role.*;

/**
 * Clears schedule for user
 * <p>
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/SCHEDULE_CLEAR")
public class ScheduleClearHandler extends AbstractScheduleHandler {
    public ScheduleClearHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    @RequiredRoles(roles = {HR, MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /SCHEDULE_CLEAR");
        final int count = scheduleService.clear(user.getId());

        return List.of(MessageBuilder.create(user)
                .line("Your schedule (%d) was cleared", count)
                .build());
    }
}
