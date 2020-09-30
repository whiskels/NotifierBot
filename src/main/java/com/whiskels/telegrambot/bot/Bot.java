package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.bot.handler.GetHandler;
import com.whiskels.telegrambot.model.Schedule;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.ScheduleService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Main bot class
 *
 * @author whiskels
 */
@Component

@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.test.name}")
    @Getter
    private String botUsername;

    @Value("${bot.test.token}")
    @Getter
    private String botToken;

    @Value("${bot.admin}")
    private String botAdmin;

    @Value("${bot.server.hour.offset}")
    private int serverHourOffset;

    private final UpdateReceiver updateReceiver;
    private final ScheduleService scheduleService;
    private final GetHandler getHandler;

    public Bot(UpdateReceiver updateReceiver, ScheduleService scheduleService, GetHandler getHandler) {
        this.updateReceiver = updateReceiver;
        this.scheduleService = scheduleService;
        this.getHandler = getHandler;
    }

    /**
     * After initialization actions:
     * - start task scheduler thread
     * - send start up report to bot admin
     */
    @PostConstruct
    public void startBot() {
        log.info(botToken);
        sendStartReport();
    }

    /**
     * Main bot method. Delegates update handling to update receiver and executes resulting messages
     *
     * @param update received by bot from users
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = updateReceiver.handle(update);

        if (messagesToSend != null && !messagesToSend.isEmpty()) {
            messagesToSend.forEach(response -> {
                if (response instanceof SendMessage) {
                    executeWithExceptionCheck((SendMessage) response);
                }
            });
        }
    }

    /**
     * Exception check for message sending
     */
    public void executeWithExceptionCheck(SendMessage sendMessage) {
        try {
            execute(sendMessage);
            log.debug("Executed {}", sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception while sending message to user: {}", e.getMessage());
        }
    }

    /**
     * Creates message to notify admin that bot has successfully started
     */
    public void sendStartReport() {
        executeWithExceptionCheck(new SendMessage()
                .setChatId(botAdmin)
                .setText("Bot start up is successful"));
        log.info("Start report sent to Admin");
    }

    @Scheduled(cron = "${bot.schedule.cron}")
    private void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now().plusHours(serverHourOffset);
        log.debug("Checking for scheduled messages");
        List<Schedule> scheduledUsers = scheduleService.isAnyScheduled(ldt.toLocalTime());
        if (!scheduledUsers.isEmpty()) {
            scheduledUsers.forEach(schedule -> {
                User user = schedule.getUser();
                log.debug("Scheduled message for {} sent at {}:{}",
                        user.getChatId(), ldt.getHour(), ldt.getMinute());

                getHandler.handle(user, null).forEach(
                        m -> executeWithExceptionCheck((SendMessage) m));
            });
        }
    }
}
