package com.whiskels.notifier.infrastructure.admin.telegram;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TextBotMessageTest {

    @Mock
    private Bot bot;

    @Test
    @DisplayName("Should call bot")
    void shouldCallBot() throws TelegramApiException {
        SendMessage message = new SendMessage();

        TextBotMessage.of(message).send(bot);

        verify(bot).execute(message);
    }

}