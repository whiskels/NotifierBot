package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.command.Command;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.bot.command.Command.SCHEDULE_GET;

@Component
@Slf4j
public class ScheduleGetHandler extends AbstractHandler {
    private final ScheduleService scheduleService;

    public ScheduleGetHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(String chatId, Message message) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        StringBuilder text = new StringBuilder();
        text.append("*Your current schedule:*%n");

        final List<Schedule> schedule = scheduleService.getSchedule(chatId);
        if (schedule == null || schedule.size() == 0) {
            text.append("No messages scheduled");
        } else {
            text.append(schedule.stream()
                    .map(e -> String.format("%02d:%02d", e.getHour(), e.getMinutes()))
                    .collect(Collectors.joining(", ")));
        }
        sendMessage.setText(text.toString());

        return Collections.singletonList(sendMessage);
    }

    @Override
    public Command supportedCommand() {
        return SCHEDULE_GET;
    }
}
