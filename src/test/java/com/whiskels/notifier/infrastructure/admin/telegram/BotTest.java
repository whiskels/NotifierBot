package com.whiskels.notifier.infrastructure.admin.telegram;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BotTest {
    @Mock
    private MessageProcessor messageProcessor;

    @InjectMocks
    private Bot bot;

    @Test
    @DisplayName("Should throw exception when document doesn't have chat id")
    void shouldThrowExceptionWhenSendingDocumentWithoutChatIt() {
        assertThrows(TelegramApiValidationException.class, () ->
                bot.execute(new SendDocument()));
    }

    @Test
    @DisplayName("Should throw exception when send message doesn't have chat id")
    void shouldThrowExceptionWhenSendingMessageWithoutChatIt() {
        assertThrows(TelegramApiValidationException.class, () ->
                bot.execute(new SendMessage()));
    }

    @Test
    @DisplayName("Should execute message if it is present")
    void shouldExecuteMessage() {
        Update update = new Update();
        var messageMock = mock(BotMessage.class);
        when(messageProcessor.onUpdateReceived(any())).thenReturn(messageMock);

        bot.onUpdateReceived(update);

        verify(messageProcessor).onUpdateReceived(update);
        verify(messageMock).send(bot);
    }

    @Test
    @DisplayName("Should not execute message if it is not present")
    void shouldNotExecuteMessageIfItIsNotPresent() {
        Update update = new Update();

        bot.onUpdateReceived(update);

        verify(messageProcessor).onUpdateReceived(update);
    }
}