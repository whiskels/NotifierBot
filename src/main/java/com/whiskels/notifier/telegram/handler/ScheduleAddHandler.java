package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.telegram.Command.*;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;
import static com.whiskels.notifier.telegram.util.ParsingUtil.getTime;

@Slf4j
@Service
@ConditionalOnBean(ScheduledCommandHandler.class)
class ScheduleAddHandler implements CommandHandler {
    private final String emptySchedule;
    private final ScheduleService scheduleService;

    public ScheduleAddHandler(@Value("${telegram.bot.schedule.empty:Empty}") String emptySchedule,
                              ScheduleService scheduleService) {
        this.emptySchedule = emptySchedule;
        this.scheduleService = scheduleService;
    }

    @Override
    @Secured({HR, MANAGER, HEAD, ADMIN})
    public SendMessage handle(User user, String message) {
        if (!message.contains(" ")) return inlineKeyboardMessage(user);

        MessageBuilder builder = builder(user);
        try {
            var time = getTime(extractArguments(message));

            log.debug("Adding schedule {}:{} to {}", time.getHours(), time.getMinutes(), user.getChatId());

            scheduleService.addSchedule(new Schedule(time.getHours(), time.getMinutes(), null), user.id());
            builder.line("Scheduled status messages to be sent daily at *%02d:%02d*", time.getHours(), time.getMinutes());
        } catch (Exception e) {
            builder.line("You've entered invalid time")
                    .line("Please try again");
            log.warn("Incorrect schedule time {} from {}", message, user.getChatId());
        }

        return builder.build();
    }

    @Override
    public Command getCommand() {
        return SCHEDULE;
    }

    /**
     * Returns predefined list of options and information on empty command
     */
    private SendMessage inlineKeyboardMessage(User user) {
        String currentSchedule = emptySchedule;

        final List<Schedule> schedule = scheduleService.getSchedule(user.getChatId());
        if (schedule != null && !schedule.isEmpty()) {
            currentSchedule = schedule.stream()
                    .map(e -> String.format("%02d:%02d", e.getHour(), e.getMinutes()))
                    .collect(Collectors.joining(", "));
        }

        return builder(user)
                .line("*Your current schedule:*")
                .line(currentSchedule)
                .line()
                .line("Choose from available options or add preferred time to [/schedule](/schedule) command:")
                .row()
                .buttonWithArguments("9:00", SCHEDULE)
                .buttonWithArguments("12:00", SCHEDULE)
                .buttonWithArguments("15:00", SCHEDULE)
                .row()
                .button("Clear schedule", SCHEDULE_CLEAR)
                .button("Help", SCHEDULE_HELP)
                .build();
    }
}
