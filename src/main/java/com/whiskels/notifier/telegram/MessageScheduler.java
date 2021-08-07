package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.annotation.Schedulable;
import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.orchestrator.SchedulableHandlerOrchestrator;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Allows bot to send scheduled messages
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("telegram-common")
@ConditionalOnBean(annotation = Schedulable.class)
public class MessageScheduler {
    private final ScheduleService scheduleService;
    private final Clock clock;
    private final SchedulableHandlerOrchestrator orchestrator;

    @Scheduled(cron = "${telegram.bot.schedule.cron:0 * * * * MON-FRI}", zone = "${common.timezone}")
    public void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now(clock);
        log.debug("Checking for scheduled messages: {}", ldt);
        List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
        if (!scheduledUsers.isEmpty()) {
            scheduledUsers.forEach(schedule -> {
                final User user = schedule.getUser();
                orchestrator.operate(user);
            });
        }
    }
}
