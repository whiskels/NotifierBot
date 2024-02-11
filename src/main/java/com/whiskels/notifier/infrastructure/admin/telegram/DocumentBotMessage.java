package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Value(staticConstructor = "of")
public class DocumentBotMessage implements BotMessage {
    SendDocument message;

    @Override
    @SneakyThrows
    public void send(Bot bot) {
        bot.execute(message);
    }
}
