package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
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
import static com.whiskels.telegrambot.util.TelegramUtils.createMessageTemplate;

@Component
@Slf4j
public class ScheduleGetHandler extends AbstractScheduleHandler {
    public ScheduleGetHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, String message) {
        SendMessage sendMessage = createMessageTemplate(user);

        StringBuilder text = new StringBuilder();
        text.append("*Your current schedule:*").append(END_LINE);

        final List<Schedule> schedule = scheduleService.getSchedule(user.getChatId());
        if (schedule == null || schedule.isEmpty()) {
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
