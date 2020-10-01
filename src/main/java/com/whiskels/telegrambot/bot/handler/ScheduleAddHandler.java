package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
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
import static com.whiskels.telegrambot.util.ParsingUtil.getTime;
import static com.whiskels.telegrambot.util.TelegramUtil.*;

/**
 * Adds schedule to the user and shows current schedule times
 *
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/SCHEDULE")
public class ScheduleAddHandler extends AbstractScheduleHandler {

    public ScheduleAddHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (!message.contains(" ")) {
            log.debug("Preparing /SCHEDULE (no args)");
            return List.of(inlineKeyboardMessage(user));
        }

        log.debug("Preparing /SCHEDULE (with args)");
        SendMessage sendMessage = createMessageTemplate(user);
        StringBuilder text = new StringBuilder();
        try {
            List<Integer> time = getTime(extractArguments(message));
            final int hours = time.get(0);
            final int minutes = time.get(1);

            log.debug("Adding schedule {}:{} to {}", hours, minutes, user.getChatId());

            scheduleService.addSchedule(new Schedule(hours, minutes, null), user.id());
            text.append("Scheduled status messages to").append(END_LINE)
                    .append("be sent daily at ")
                    .append(String.format("*%02d:%02d*%n", hours, minutes))
                    .append("To reset schedule use '/schedule_clear' command");

        } catch (Exception e) {
            text.append("You've entered invalid time")
                    .append(END_LINE)
                    .append("Please try again");
            log.debug("Incorrect schedule time {}", message);
        }

        sendMessage.setText(text.toString());
        return Collections.singletonList(sendMessage);
    }

    /**
     * Returns predefined list of options and information on empty command
     */
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
                .append("Choose from available options or add preferred time to [/schedule](/schedule) command:");

        return createMessageTemplate(user).setText(text.toString())
                .setReplyMarkup(inlineKeyboardMarkup);
    }
}
