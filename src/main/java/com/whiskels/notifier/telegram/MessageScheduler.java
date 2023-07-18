package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ScheduledCommandHandler.class)
class MessageScheduler {
    private final ScheduleService scheduleService;
    private final Clock clock;
    private final ScheduledHandlerOrchestrator orchestrator;

    @Scheduled(cron = "${telegram.bot.schedule.cron:0 * * * * MON-FRI}", zone = "${common.timezone}")
    public void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now(clock);
        log.debug("Checking for scheduled messages: {}", ldt);
        Set<User> scheduledUsers = scheduleService.findScheduled(ldt.toLocalTime()).stream()
                .map(Schedule::getUser)
                .collect(Collectors.toSet());
        orchestrator.operate(scheduledUsers);
    }
}
