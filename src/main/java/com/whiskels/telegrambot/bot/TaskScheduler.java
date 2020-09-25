package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.handler.GetHandler;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * Thread used to check for scheduled tasks once a minute and:
 * - execute scheduled messages (on workdays)
 * - update customer info
 */
@Component
@PropertySource({"classpath:external/json.properties", "classpath:bot/bot.properties"})
@Slf4j
public class TaskScheduler implements Runnable {
    private static final int UPDATE_DELAY = 60_000;

    @Value("${bot.server.hour.offset}")
    private int serverHourOffset;

    @Value("${json.update.hour}")
    private int customerUpdateHour;

    @Value("${json.update.minutes}")
    private int customerUpdateMinutes;

    private final GetHandler getHandler;
    private final ScheduleService scheduleService;
    private final ApplicationContext applicationContext;

    public TaskScheduler(GetHandler getHandler, ScheduleService scheduleService, ApplicationContext applicationContext) {
        this.getHandler = getHandler;
        this.scheduleService = scheduleService;
        this.applicationContext = applicationContext;
    }

    /**
     * Main loop.
     * Checks for scheduled tasks, then sleeps for {@link #UPDATE_DELAY}
     */
    @Override
    public void run() {
        log.info("[STARTED] TaskScheduler.");
        while (true) {
            processScheduledTasks();

            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException e) {
                log.error("Interrupted: {}", e);
                return;
            }
        }
    }

    /**
     * Analyzes if there are any scheduled tasks on workdays
     */
    private void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now().plusHours(serverHourOffset);
        if (ldt.getDayOfWeek() != SUNDAY && ldt.getDayOfWeek() != SATURDAY) {
            log.debug("Checking for scheduled messages");

            List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
            if (!scheduledUsers.isEmpty()) {
                for (Schedule schedule : scheduledUsers) {
                    final User user = schedule.getUser();
                    log.debug("Scheduled message for {} sent at {}:{}", user.getChatId(), ldt.getHour(), ldt.getMinute());
                    getHandler.handle(user, null).forEach(
                            m -> applicationContext.getBean(Bot.class)
                                    .executeWithExceptionCheck((SendMessage) m)
                    );
                }
            }
        }

        // Update Customer info daily at specified time
        if (ldt.getHour() == customerUpdateHour && ldt.getMinute() == customerUpdateMinutes) {
            getHandler.updateCustomers();
            log.debug("Updated customer info");
        }
    }
}

