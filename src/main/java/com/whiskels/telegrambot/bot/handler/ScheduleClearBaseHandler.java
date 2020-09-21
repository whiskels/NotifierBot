package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.SCHEDULE_CLEAR;

@Component
@Slf4j
public class ScheduleClearBaseHandler extends AbstractScheduleHandler {
    public ScheduleClearBaseHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, Message message) {
        scheduleService.clear(user.getId());

        return Collections.singletonList(createMessageTemplate(user)
                .setText("Your schedule was cleared"));
    }

    @Override
    public Command supportedCommand() {
        return SCHEDULE_CLEAR;
    }
}
