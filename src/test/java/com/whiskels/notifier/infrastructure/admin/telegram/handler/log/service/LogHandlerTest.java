package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.DocumentBotMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogHandlerTest {
    @Mock
    private LogService logService;

    @InjectMocks
    private LogHandler logHandler;

    @Test
    @DisplayName("Should prepare correct message with logs attached")
    void shouldPrepareLogMessage() throws Exception {
        final var userId = "user1";
        final var message = "message";
        final byte[] fileContent = new byte[]{84, 101, 115, 116};
        when(logService.getLogsAsByteArray()).thenReturn(fileContent);

        var actual = ((DocumentBotMessage) logHandler.handle(userId, message)).getMessage();

        assertEquals(actual.getChatId(), userId);
        assertEquals(actual.getDocument().getAttachName(), "attach://logs.txt");
        assertEquals(actual.getCaption(), "Application logs");
        assertArrayEquals(actual.getDocument().getNewMediaStream().readAllBytes(), fileContent);
    }

    @Test
    @DisplayName("Should return correct command for log handler")
    void shouldReturnCorrectCommandForLogHandler() {
        Command command = logHandler.getCommand();

        assertEquals(Command.GET_LOGS, command);
    }
}