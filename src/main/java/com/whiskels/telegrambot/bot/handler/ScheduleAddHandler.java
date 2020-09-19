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

import static com.whiskels.telegrambot.bot.command.Command.SCHEDULE;

@Component
@Slf4j
public class ScheduleAddHandler extends AbstractHandler {
    private final ScheduleService scheduleService;

    public ScheduleAddHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(String chatId, Message message) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        String value = message.getText();

        StringBuilder text = new StringBuilder();
        try {
            int hours = 0;
            int minutes = 0;
            int len = value.length();

            if (len > 4) {
                throw new IllegalArgumentException();
            } else if (len == 1 || len == 3) {
                hours = Integer.parseInt(value.substring(0, 1));
                if (len == 3) {
                    minutes = Integer.parseInt(value.substring(1));
                }
            } else if (len == 2) {
                hours = Integer.parseInt(value);
            } else if (len == 4) {
                hours = Integer.parseInt(value.substring(0, 2));
                minutes = Integer.parseInt(value.substring(2));
            }

            if (hours > 24 || hours < 0 || minutes < 0 || minutes > 60) {
                throw new IllegalArgumentException();
            }

            scheduleService.addSchedule(chatId, hours, minutes);
            text.append("Scheduled status messages to%n")
                    .append("be sent daily at ")
                    .append(String.format("*%02d:%02d*%n", hours, minutes))
                    .append("To reset schedule use '/schedule clear' command");
        } catch (Exception e) {
            text.append("You've entered invalid time%n");
            text.append("Please try again");
            log.debug("Incorrect schedule time {}", message);
        }
        sendMessage.setText(text.toString());

        return Collections.singletonList(sendMessage);
    }


    @Override
    public Command supportedCommand() {
        return SCHEDULE;
    }
}
