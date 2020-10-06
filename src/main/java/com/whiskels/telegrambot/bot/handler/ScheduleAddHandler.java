package com.whiskels.telegrambot.bot.handler;

import com.whiskels.telegrambot.bot.BotCommand;
import com.whiskels.telegrambot.bot.builder.MessageBuilder;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.security.RequiredRoles;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.telegrambot.model.Role.*;
import static com.whiskels.telegrambot.util.ParsingUtil.getTime;
import static com.whiskels.telegrambot.util.TelegramUtil.extractArguments;

/**
 * Adds schedule to the user and shows current schedule times
 * <p>
 * Available to: Manager, Head, Admin
 */
@Component
@Slf4j
@BotCommand(command = "/SCHEDULE", message = "Manage schedule")
public class ScheduleAddHandler extends AbstractScheduleHandler {

    public ScheduleAddHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
    @RequiredRoles(roles = {MANAGER, HEAD, ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        if (!message.contains(" ")) {
            log.debug("Preparing /SCHEDULE (no args)");
            return List.of(inlineKeyboardMessage(user));
        }

        log.debug("Preparing /SCHEDULE (with args)");

        MessageBuilder builder = MessageBuilder.create(user);
        try {
            List<Integer> time = getTime(extractArguments(message));
            final int hours = time.get(0);
            final int minutes = time.get(1);

            log.debug("Adding schedule {}:{} to {}", hours, minutes, user.getChatId());

            scheduleService.addSchedule(new Schedule(hours, minutes, null), user.id());
            builder.line("Scheduled status messages to")
                    .line("be sent daily at *%02d:%02d*", hours, minutes);
        } catch (Exception e) {
            builder.line("You've entered invalid time")
                    .line("Please try again");
            log.debug("Incorrect schedule time {}", message);
        }

        return List.of(builder.build());
    }

    /**
     * Returns predefined list of options and information on empty command
     */
    private SendMessage inlineKeyboardMessage(User user) {
        String currentSchedule = "No messages scheduled";

        final List<Schedule> schedule = scheduleService.getSchedule(user.getChatId());
        if (schedule != null && !schedule.isEmpty()) {
            currentSchedule = schedule.stream()
                    .map(e -> String.format("%02d:%02d", e.getHour(), e.getMinutes()))
                    .collect(Collectors.joining(", "));
        }

        return MessageBuilder.create(user)
                .line("*Your current schedule:*")
                .line(currentSchedule)
                .line()
                .line("Choose from available options or add preferred time to [/schedule](/schedule) command:")
                .row()
                .buttonWithArguments("9:00", "/SCHEDULE")
                .buttonWithArguments("12:00", "/SCHEDULE")
                .buttonWithArguments("15:00", "/SCHEDULE")
                .row()
                .button("Clear schedule", "/SCHEDULE_CLEAR")
                .button("Help", "/SCHEDULE_HELP")
                .build();
    }
}
