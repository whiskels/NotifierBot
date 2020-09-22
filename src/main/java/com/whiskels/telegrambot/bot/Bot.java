package com.whiskels.telegrambot.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * Main bot class
 *
 * @author whiskels
 */
@Component
@PropertySource("classpath:bot/bot.properties")
@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name.war}")
    @Getter
    private String botUsername;

    @Value("${bot.token.war}")
    @Getter
    private String botToken;

    @Value("${bot.admin}")
    private String botAdmin;

    private final UpdateReceiver updateReceiver;
    private final MessageScheduler messageScheduler;


    public Bot(UpdateReceiver updateReceiver, MessageScheduler messageScheduler) {
        this.updateReceiver = updateReceiver;
        this.messageScheduler = messageScheduler;
    }

    /**
     * After initialization schedule message scheduler thread is started running and bot sends start up report to bot admin
     */
    @PostConstruct
    public void startBot() {
        Thread messageSchedulerThread = new Thread(messageScheduler);
        messageSchedulerThread.setDaemon(true);
        messageSchedulerThread.setName("messageScheduler");
        messageSchedulerThread.setPriority(3);
        messageSchedulerThread.start();

        sendStartReport();
    }

    /**
     * Main bot method. Delegated update handling to update receiver and executes resulting messages
     *
     * @param update received by bot from users
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = updateReceiver.handle(update);

        if (messagesToSend != null || !messagesToSend.isEmpty()) {
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
     * Creates message to notify admin that bot successfully started
     */
    public void sendStartReport() {
        executeWithExceptionCheck(new SendMessage()
                .setChatId(botAdmin)
                .setText("Bot start up is successful"));
        log.info("Start report sent to Admin");
    }
}
