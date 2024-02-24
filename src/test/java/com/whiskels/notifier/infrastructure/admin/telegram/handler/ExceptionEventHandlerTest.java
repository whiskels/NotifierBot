package com.whiskels.notifier.infrastructure.admin.telegram.handler;

import com.whiskels.notifier.infrastructure.admin.telegram.Bot;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service.LogHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.retry.RetryHandler;
import com.whiskels.notifier.reporting.exception.ExceptionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionEventHandlerTest {
    private static final String BOT_ADMIN = "TEST";

    @Mock
    private Bot bot;

    @Mock
    private RetryHandler retryHandler;

    @Mock
    private LogHandler logHandler;

    @Test
    @DisplayName("Should prepare message with log button if log handler is present")
    void shouldPrepareMessageWithLogButton() throws Exception {
        final var handler = new ExceptionEventHandler(BOT_ADMIN, bot, retryHandler, logHandler);
        final var expected = """
                {
                    "chat_id": "TEST",
                    "text": "TEST",
                    "reply_markup": {
                        "inline_keyboard": [
                            [
                                {
                                    "text": "🔁 Retry reports:EMPLOYEE_EVENT",
                                    "callback_data": "RETRY_REPORT EMPLOYEE_EVENT"
                                }
                            ],
                            [
                                {
                                    "text": "🕹 Fetch logs",
                                    "callback_data": "GET_LOGS"
                                }
                            ]
                        ]
                    },
                    "method": "sendmessage"
                }""";

        when(logHandler.getCommand()).thenReturn(Command.GET_LOGS);

        testHandle(handler, expected);
    }

    @Test
    @DisplayName("Should prepare message without log handler if it is not available")
    void shouldPrepareMessageWithoutLogHandler() throws Exception {
        final var handler = new ExceptionEventHandler(BOT_ADMIN, bot, retryHandler, null);
        final var expected = """
                {
                    "chat_id": "TEST",
                    "text": "TEST",
                    "reply_markup": {
                        "inline_keyboard": [
                            [
                                {
                                    "text": "🔁 Retry reports:EMPLOYEE_EVENT",
                                    "callback_data": "RETRY_REPORT EMPLOYEE_EVENT"
                                }
                            ]
                        ]
                    },
                    "method": "sendmessage"
                }""";

        testHandle(handler, expected);
    }

    void testHandle(ExceptionEventHandler handler, String expectedJson) throws Exception {
        final var event = ExceptionEvent.of("TEST", EMPLOYEE_EVENT);
        when(retryHandler.getCommand()).thenReturn(Command.RETRY_REPORT);

        handler.handle(event);

        verify(bot).execute(eq(MAPPER.readValue(expectedJson, SendMessage.class)));
    }
}