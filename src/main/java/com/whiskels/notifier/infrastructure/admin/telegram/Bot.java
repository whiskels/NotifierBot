package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Profile("telegram")
public class Bot extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;
    private final MessageProcessor messageProcessor;

    public Bot(@Value("${telegram.bot.name:NotifierAdmin}") String botUsername,
               @Value("${telegram.bot.token}") String botToken,
               MessageProcessor messageProcessor) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void onUpdateReceived(Update update) {
       var message = messageProcessor.onUpdateReceived(update);
       if (nonNull(message)) {
           message.send(this);
       }
    }
}
