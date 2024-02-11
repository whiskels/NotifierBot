package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.SneakyThrows;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Value(staticConstructor = "of")
public class TextBotMessage implements BotMessage {
    SendMessage message;

    @Override
    @SneakyThrows
    public void send(Bot bot) {
        bot.execute(message);
    }
}
