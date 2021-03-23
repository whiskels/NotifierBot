package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractScheduleHandler;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.ParsingUtil.extractArguments;
import static com.whiskels.notifier.common.ParsingUtil.getTime;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Adds schedule to the user and shows current schedule times
 * <p>
 * Available to: HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = "/SCHEDULE", message = "Manage schedule", requiredRoles = {HR, MANAGER, HEAD, ADMIN})
@ConditionalOnBean(annotation = Schedulable.class)
public class ScheduleAddHandler extends AbstractScheduleHandler {

    public ScheduleAddHandler(ScheduleService scheduleService) {
        super(scheduleService);
    }

    @Override
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
