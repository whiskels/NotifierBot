package com.whiskels.notifier.infrastructure.admin.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {
    private static final String BOT_ADMIN = "2";

    @Mock
    private CommandHandler defaultHandler;

    @Mock
    private CommandHandler customHandler;

    private MessageProcessor messageProcessor;

    @BeforeEach
    void initProcessor() {
        when(customHandler.getCommand()).thenReturn(Command.GET_LOGS);

        messageProcessor = new MessageProcessor(BOT_ADMIN, List.of(customHandler), defaultHandler);
    }

    @Test
    @DisplayName("Should not handle message from unauthorized user")
    void shouldNotHandleMessageFromUnauthorizedUser() {
        var update = prepareMessageUpdate(1L, "GET_LOGS UNAUTHORIZED");

        messageProcessor.onUpdateReceived(update);

        verifyNoInteractions(defaultHandler);
        verify(customHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should handle message with default handler")
    void shouldHandleWithDefaultHandler() {
        var update = prepareMessageUpdate(2L, "DEFAULT DEFAULT");

        messageProcessor.onUpdateReceived(update);

        verify(defaultHandler).handle(BOT_ADMIN, "DEFAULT DEFAULT");
        verify(customHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should handle unsupported command with default handler")
    void shouldUHandleUnsupportedCommandWithDefaultHandler() {
        var update = prepareMessageUpdate(2L, "UNSUPPORTED UNSUPPORTED");

        messageProcessor.onUpdateReceived(update);

        verify(defaultHandler).handle(BOT_ADMIN, "UNSUPPORTED UNSUPPORTED");
        verify(customHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should handle message with specific handler")
    void shouldHandleMessageWithSpecificHandler() {
        var update = prepareMessageUpdate(2L, "GET_LOGS TEST");

        messageProcessor.onUpdateReceived(update);

        verify(customHandler).handle(BOT_ADMIN, "GET_LOGS TEST");
        verifyNoInteractions(defaultHandler);
    }

    @Test
    @DisplayName("Should not handle callback query from unauthorized user")
    void shouldNotHandleCallbackQueryFromUnauthorizedUser() {
        var update = prepareCallbackUpdate(1L, "GET_LOGS UNAUTHORIZED");

        messageProcessor.onUpdateReceived(update);

        verifyNoInteractions(defaultHandler);
        verify(customHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should handle callback query with default handler")
    void shouldHandleCallbackQueryWithDefaultHandler() {
        var update = prepareCallbackUpdate(2L, "DEFAULT DEFAULT");

        messageProcessor.onUpdateReceived(update);

        verify(defaultHandler).handle(BOT_ADMIN, "DEFAULT DEFAULT");
        verify(customHandler, never()).handle(any(), any());
    }

    @Test
    @DisplayName("Should handle callback query with custom handler")
    void shouldHandleCallbackQueryWithCustomHandler() {
        var update = prepareCallbackUpdate(2L, "GET_LOGS TEST");

        messageProcessor.onUpdateReceived(update);

        verify(customHandler).handle(BOT_ADMIN, "GET_LOGS TEST");
        verifyNoInteractions(defaultHandler);
    }

    @Test
    @DisplayName("Should not handle unsupported update")
    void shouldNotHandleUnsupportedUpdate() {
        var update = new Update();
        update.setMessage(new Message());

        messageProcessor.onUpdateReceived(update);

        verify(customHandler, never()).handle(any(), any());
        verifyNoInteractions(defaultHandler);
    }

    private static Update prepareMessageUpdate(Long userId, String text) {
        final var update = new Update();
        final var message = new Message();
        message.setText(text);
        final var user = new User();
        user.setId(userId);
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

    private static Update prepareCallbackUpdate(Long userId, String text) {
        final var update = new Update();
        final var callback = new CallbackQuery();
        final var user = new User();
        user.setId(userId);
        callback.setFrom(user);
        callback.setData(text);
        update.setCallbackQuery(callback);
        return update;
    }
}