package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.event.MessageReceivedEvent;
import com.whiskels.notifier.telegram.event.SendMessageCreationEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
class Bot extends TelegramLongPollingBot {
    private final ApplicationEventPublisher publisher;

    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;

    public Bot(@Value("${telegram.bot.name:TelegramNotifierBot}") String botUsername,
               @Value("${telegram.bot.token}") String botToken,
               ApplicationEventPublisher publisher) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.publisher = publisher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        publisher.publishEvent(new MessageReceivedEvent(update));
    }

    @EventListener(SendMessageCreationEvent.class)
    public void executeSafe(SendMessageCreationEvent event) {
        final SendMessage message = event.get();
        if (message == null) return;
        try {
            execute(message);
            log.debug("Executed {}", message);
        } catch (TelegramApiException e) {
            log.error("Exception while sending message {} to user: {}", message, e.getMessage());
        }
    }
}
