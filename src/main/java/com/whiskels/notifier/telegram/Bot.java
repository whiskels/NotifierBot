package com.whiskels.notifier.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Main bot class
 *
 * @author whiskels
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    @Getter
    private String botUsername;

    @Value("${bot.token}")
    @Getter
    private String botToken;

    @Value("${bot.admin}")
    private String botAdmin;

    private final UpdateReceiver updateReceiver;

    /**
     * After initialization actions:
     * - start task scheduler thread
     * - send start up report to bot admin
     */
    @PostConstruct
    public void startBot() {
        sendStartReport();
    }

    /**
     * Main bot method. Delegates update handling to update receiver and executes resulting messages
     *
     * @param update received by bot from users
     */
    @Override
    public void onUpdateReceived(Update update) {
        List<BotApiMethod<Message>> messagesToSend = updateReceiver.handle(update);

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
            log.error("Exception while sending message {} to user: {}", sendMessage, e.getMessage());
        }
    }

    /**
     * Creates message to notify admin that bot has successfully started
     */
    public void sendStartReport() {
        executeWithExceptionCheck(new SendMessage()
                .setChatId(botAdmin)
                .setText("Bot start up is successful"));
        log.debug("Start report sent to Admin");
    }
}
