package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.TelegramUtils.*;

@Component
@Slf4j
@BotCommand(command = "/SCHEDULE")
public class ScheduleAddHandler extends AbstractScheduleHandler {

    public ScheduleAddHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    @RequiredRoles(roles = {HEAD, MANAGER, ADMIN})
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (!message.contains(" ")) {
            return Collections.singletonList(inlineKeyboardMessage(user));
        }

        SendMessage sendMessage = createMessageTemplate(user);
        String value = message.split("\\s+")[1];
        StringBuilder text = new StringBuilder();
        try {
            int hours = 0;
            int minutes = 0;
            int len = value.length();

            if (value.contains(":")) {
                int delimeter = value.indexOf(":");
                hours = Integer.parseInt(value.substring(0, delimeter));
                minutes = Integer.parseInt(value.substring(++delimeter));
            } else {
                if (len > 5) {
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
            }

            if (hours > 24 || hours < 0 || minutes < 0 || minutes > 60) {
                throw new IllegalArgumentException();
            }

            log.debug("Adding schedule {}:{} to {}", hours, minutes, user.getChatId());

            scheduleService.addSchedule(new Schedule(hours, minutes, null), user.id());
            text.append("Scheduled status messages to").append(END_LINE)
                    .append("be sent daily at ")
                    .append(String.format("*%02d:%02d*%n", hours, minutes))
                    .append("To reset schedule use '/schedule clear' command");

        } catch (IllegalArgumentException e1) {
            text.append("You've entered invalid time")
                    .append(END_LINE)
                    .append("Please try again");
            log.debug("Incorrect schedule time {}", message);
        } catch (Exception e) {
            text.append("You've entered invalid time")
                    .append(END_LINE)
                    .append("Please try again");
            log.debug("Incorrect schedule time {}", message);
        }

        sendMessage.setText(text.toString());
        return Collections.singletonList(sendMessage);
    }

    private SendMessage inlineKeyboardMessage(User user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("9:00", "/SCHEDULE", true),
                createInlineKeyboardButton("12:00", "/SCHEDULE", true),
                createInlineKeyboardButton("15:00", "/SCHEDULE", true));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, SCHEDULE_MANAGE_ROW));

        StringBuilder text = new StringBuilder();
        text.append("*Your current schedule:*")
                .append(END_LINE);
        final List<Schedule> schedule = scheduleService.getSchedule(user.getChatId());
        if (schedule == null || schedule.isEmpty()) {
            text.append("No messages scheduled");
        } else {
            text.append(schedule.stream()
                    .map(e -> String.format("%02d:%02d", e.getHour(), e.getMinutes()))
                    .collect(Collectors.joining(", ")));
        }
        text.append(END_LINE)
                .append(END_LINE)
                .append("Add preferred notification time or choose from available options:");

        return createMessageTemplate(user).setText(text.toString())
                .setReplyMarkup(inlineKeyboardMarkup);
    }
}
