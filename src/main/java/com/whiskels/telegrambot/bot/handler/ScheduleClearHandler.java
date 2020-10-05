package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtil.createMessageTemplate;

/**
 * Clears schedule for user
 *
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
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /SCHEDULE_CLEAR");
        scheduleService.clear(user.getId());

        return Collections.singletonList(createMessageTemplate(user)
                .setText("Your schedule was cleared"));
    }
}
