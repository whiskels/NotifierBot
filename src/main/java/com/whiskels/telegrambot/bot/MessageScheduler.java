package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.annotations.Schedulable;
import com.whiskels.telegrambot.bot.handler.AbstractBaseHandler;
import com.whiskels.telegrambot.model.Role;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
@Slf4j
public class MessageScheduler {
    @Value("${bot.server.hour.offset}")
    private int serverHourOffset;

    private final Bot bot;
    private final ScheduleService scheduleService;
    private final List<AbstractBaseHandler> handlers;

    public MessageScheduler(Bot bot, ScheduleService scheduleService, List<AbstractBaseHandler> handlers) {
        this.bot = bot;
        this.scheduleService = scheduleService;
        this.handlers = handlers;
    }

    @Scheduled(cron = "${bot.cron}")
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
