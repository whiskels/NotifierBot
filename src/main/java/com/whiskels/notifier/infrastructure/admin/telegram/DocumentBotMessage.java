package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.SneakyThrows;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

@Value(staticConstructor = "of")
public class DocumentBotMessage implements BotMessage {
    SendDocument message;

    @Override
    @SneakyThrows
    public void send(Bot bot) {
        bot.execute(message);
    }
}
