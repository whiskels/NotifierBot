package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.command.GetHandler;
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

@Component
@PropertySource("classpath:external/json.properties")
@Slf4j
public class MessageScheduler implements Runnable {
    private static final int UPDATE_DELAY = 60_000;
    private static final int SERVER_HOUR_OFFSET = 3;

    @Value("${json.update.hour}")
    private int CUSTOMER_UPDATE_HOUR;

    @Value("${json.update.minutes}")
    private int CUSTOMER_UPDATE_MINUTES;


    private GetHandler getHandler;
    private ScheduleService scheduleService;
    private ApplicationContext applicationContext;

    public MessageScheduler(GetHandler getHandler, ScheduleService scheduleService, ApplicationContext applicationContext) {
        this.getHandler = getHandler;
        this.scheduleService = scheduleService;
        this.applicationContext = applicationContext;
    }

    /*
     * Scheduler's main loop
     */
    @Override
    public void run() {
        log.info(String.format("[STARTED] Scheduler."));
        while (true) {
            processScheduledTasks();

            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    /*
     * Used to send scheduled messages
     */
    private void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now().plusHours(SERVER_HOUR_OFFSET);
        if (ldt.getDayOfWeek() != SUNDAY && ldt.getDayOfWeek() != SATURDAY) {
            log.debug("Checking for scheduled messages");

            List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
            if (!scheduledUsers.isEmpty()) {
                for (Schedule schedule : scheduledUsers) {
                    final User user = schedule.getUser();
                    log.debug("Scheduled message for {} sent at {}:{}", user.getChatId(), ldt.getHour(), ldt.getMinute());
                    getHandler.operate(user, null).forEach(
                            m -> applicationContext.getBean(Bot.class)
                                    .executeWithExceptionCheck((SendMessage) m)
                    );
                }
            }
        }

        // Update Customer info daily
        if (ldt.getHour() == CUSTOMER_UPDATE_HOUR && ldt.getMinute() == CUSTOMER_UPDATE_MINUTES) {
            getHandler.updateCustomers();
            log.debug("Updated customer info");
        }
    }
}

