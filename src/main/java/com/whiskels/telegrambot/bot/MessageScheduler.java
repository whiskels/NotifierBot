package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.handler.GetHandler;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
@PropertySource("classpath:external/json.properties")
@Slf4j
public class MessageScheduler implements Runnable {
    private static final int UPDATE_DELAY = 60_000;

    @Value("${json.update.hour}")
    private int CUSTOMER_UPDATE_HOUR;

    @Value("${json.update.minutes}")
    private int CUSTOMER_UPDATE_MINUTES;


    private GetHandler getHandler;
    private ScheduleService scheduleService;
    private Bot bot;

    public MessageScheduler(GetHandler getHandler, ScheduleService scheduleService, Bot bot) {
        this.getHandler = getHandler;
        this.scheduleService = scheduleService;
        this.bot = bot;
    }

    /*
     * Scheduler's main loop
     */
    @Override
    public void run() {
        log.info(String.format("[STARTED] TaskScheduler.  Bot class: %s", bot));
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
        final LocalDateTime ldt = LocalDateTime.now().plusHours(3);
        if (ldt.getDayOfWeek() != DayOfWeek.SUNDAY && ldt.getDayOfWeek() != DayOfWeek.SATURDAY) {
            log.debug("Checking for scheduled messages");

            List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
            if (!scheduledUsers.isEmpty()) {
                for (Schedule schedule : scheduledUsers) {
                    final String chatId = schedule.getUser().getChatId();
                    log.debug("Scheduled message for {} sent at {}:{}", chatId, ldt.getHour(), ldt.getMinute());
                    getHandler.operate(chatId, null).forEach(
                            m -> bot.executeWithExceptionCheck((SendMessage) m)
                    );
                }
            }
        }

        // Update Customer info daily
        if (ldt.getHour() == CUSTOMER_UPDATE_HOUR && ldt.getMinute() == CUSTOMER_UPDATE_MINUTES) {
            bot.updateCustomers();
            log.debug("Updated customer info");
        }
    }
}

