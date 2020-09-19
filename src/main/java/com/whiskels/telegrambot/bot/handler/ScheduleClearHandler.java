package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.SCHEDULE_CLEAR;

@Component
@Slf4j
public class ScheduleClearHandler extends AbstractHandler {
    private final ScheduleService scheduleService;

    public ScheduleClearHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(String chatId, Message message) {
        scheduleService.clearSchedule(chatId);

        return Collections.singletonList(createMessageTemplate(chatId)
                .setText("Your schedule was cleared"));
    }

    @Override
    public Command supportedCommand() {
        return SCHEDULE_CLEAR;
    }
}
