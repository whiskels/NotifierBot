package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Component
@Slf4j
@BotCommand(command = "/SCHEDULE_CLEAR")
public class ScheduleClearBaseHandler extends AbstractScheduleHandler {
    public ScheduleClearBaseHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    @RequiredRoles(roles = {HEAD, MANAGER, ADMIN})
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        scheduleService.clear(user.getId());

        return Collections.singletonList(createMessageTemplate(user)
                .setText("Your schedule was cleared"));
    }
}
