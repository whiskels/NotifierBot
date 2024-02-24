package com.whiskels.notifier.infrastructure.admin.telegram.handler;

import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.CommandHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.assertEqualsWithJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultHandlerTest {
    @Mock
    private CommandHandler mockOne;
    @Mock
    private CommandHandler mockTwo;

    private DefaultHandler defaultHandler;

    @BeforeEach
    void initHandler() {
        defaultHandler = new DefaultHandler(List.of(mockOne, mockTwo));
    }

    @Test
    @DisplayName("Should prepare correct response in default handler")
    void shouldPrepareCorrectResponseInDefaultHandler() {
        final var userId = "user1";
        final var message = "message";
        final var expected = """
                {
                  "chat_id" : "user1",
                  "text" : "👋 Welcome to admin module\\nHere is what you can do\\n",
                  "reply_markup" : {
                    "inline_keyboard" : [ [ {
                      "text" : "🕹 Fetch logs",
                      "callback_data" : "GET_LOGS"
                    } ], [ {
                      "text" : "📈 Reload data",
                      "callback_data" : "RELOAD_DATA"
                    } ] ]
                  },
                  "method" : "sendmessage"
                }""";

        when(mockOne.getCommand()).thenReturn(Command.GET_LOGS);
        when(mockTwo.getCommand()).thenReturn(Command.RELOAD_DATA);

        var botMessage = ((TextBotMessage) defaultHandler.handle(userId, message)).getMessage();

        assertEqualsWithJson(expected, botMessage);
    }

    @Test
    @DisplayName("Should return correct command for default handler")
    void shouldReturnCorrectCommandForDefaultHandler() {
        assertEquals(defaultHandler.getCommand(), Command.DEFAULT);
    }

}