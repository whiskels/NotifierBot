package com.whiskels.notifier.telegram;

import com.whiskels.notifier.model.Role;
import com.whiskels.notifier.model.Schedule;
import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.ScheduleService;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Allows bot to send scheduled messages
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"telegram", "telegram-test"})
public class MessageScheduler {
    @Value("${heroku.server.hour.offset}")
    private int serverHourOffset;

    private final Bot bot;
    private final ScheduleService scheduleService;
    private final List<AbstractBaseHandler> handlers;

    /**
     * Checks if there is any job scheduled and processes it
     */
    @Scheduled(cron = "${telegram.bot.cron}")
    private void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now().plusHours(serverHourOffset);
        log.debug("Checking for scheduled messages: {}", ldt);
        List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
        if (!scheduledUsers.isEmpty()) {
            scheduledUsers.forEach(schedule -> {
                final User user = schedule.getUser();
                getHandler(user).authorizeAndHandle(user, null)
                        .forEach(m -> bot.executeWithExceptionCheck((SendMessage) m));
                log.debug("Scheduled message for {} sent at {}:{}",
                        user.getChatId(), ldt.getHour(), ldt.getMinute());
            });
        }
    }

    /**
     * Searches for an {@link AbstractBaseHandler} that supports {@link Schedulable} annotation where
     * any of defined roles are presented in the set of {@link User} roles
     *
     * Note: current realization suggests that any user role can schedule no more than one handler
     *
     * @param user {@link User} that scheduled an event
     * @return {@link AbstractBaseHandler} that was scheduled by user
     */
    private AbstractBaseHandler getHandler(User user) {
        final Set<Role> userRoles = user.getRoles();
        return handlers.stream()
                .filter(h -> h.getClass()
                        .isAnnotationPresent(Schedulable.class))
                .filter(h -> Stream.of(h.getClass()
                        .getAnnotation(Schedulable.class)
                        .roles())
                        .anyMatch(userRoles::contains))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }
}
