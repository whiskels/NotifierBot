package com.whiskels.notifier.infrastructure.admin.telegram.handler.retry;

import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.assertEqualsWithJson;
import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_BIRTHDAY;
import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryHandlerTest {
    @Mock
    private ReportServiceImpl reportService;

    @InjectMocks
    private RetryHandler retryHandler;

    @Test
    @DisplayName("Should return menu message for message without context")
    void shouldReturnMenuForMessageWithoutContext() {
        final var expected = """
                {
                  "chat_id" : "user1",
                  "text" : "Choose report to retry",
                  "reply_markup" : {
                    "inline_keyboard" : [ [ {
                      "text" : "CUSTOMER_BIRTHDAY",
                      "callback_data" : "RETRY_REPORT CUSTOMER_BIRTHDAY"
                    } ], [ {
                      "text" : "EMPLOYEE_EVENT",
                      "callback_data" : "RETRY_REPORT EMPLOYEE_EVENT"
                    } ] ]
                  },
                  "method" : "sendmessage"
                }""";
        final var userId = "user1";
        final var message = "noSpaceMessage";
        when(reportService.getReportTypes()).thenReturn(List.of(CUSTOMER_BIRTHDAY, EMPLOYEE_EVENT));

        var botMessage = ((TextBotMessage) retryHandler.handle(userId, message)).getMessage();

        assertEqualsWithJson(expected, botMessage);
    }

    @Test
    @DisplayName("Should return correct message for message with context")
    void shouldReturnCorrectResponseForMessageWithContext() {
        final var expected = """
                {
                  "chat_id" : "user1",
                  "text" : "Retried EMPLOYEE_EVENT",
                  "method" : "sendmessage"
                }""";
        final var userId = "user1";
        final var message = "command EMPLOYEE_EVENT";

        var botMessage = ((TextBotMessage) retryHandler.handle(userId, message)).getMessage();

        verify(reportService).executeReport(EMPLOYEE_EVENT);
        assertEqualsWithJson(expected, botMessage);
    }

    @Test
    @DisplayName("Should return correct command for Retry handler")
    void shouldReturnCorrectCommandForRetryHandler() {
        Command command = retryHandler.getCommand();

        assertEquals(Command.RETRY_REPORT, command);
    }
}