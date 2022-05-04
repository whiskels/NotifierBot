package com.whiskels.notifier.telegram;

import com.whiskels.notifier.common.CreationEvent;
import com.whiskels.notifier.telegram.event.SendMessageCreationEvent;
import com.whiskels.notifier.telegram.event.UpdateCreationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Main Telegram bot class
 *
 * @author whiskels
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("telegram-common")
public class Bot extends TelegramLongPollingBot {
    @Value("${telegram.bot.name:TelegramNotifierBot}")
    @Getter
    private String botUsername;

    @Value("${telegram.bot.token}")
    @Getter
    private String botToken;

    private final ApplicationEventPublisher publisher;

    @Override
    public void onUpdateReceived(Update update) {
        publisher.publishEvent(new UpdateCreationEvent(update));
    }

    @EventListener(SendMessageCreationEvent.class)
    public void executeSafe(CreationEvent<SendMessage> event) {
        final SendMessage message = event.get();
        try {
            execute(message);
            log.debug("Executed {}", message);
        } catch (TelegramApiException e) {
            log.error("Exception while sending message {} to user: {}", message, e.getMessage());
        }
    }
}
