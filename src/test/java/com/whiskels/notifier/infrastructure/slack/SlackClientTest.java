package com.whiskels.notifier.infrastructure.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SlackClientTest {

    @Mock
    private Slack slack;

    @InjectMocks
    private SlackClient slackClient;


    @Test
    @DisplayName("Should send payload")
    void testSendSuccess() throws IOException {
        String webhook = "https://hooks.slack.com/services/...";
        Payload payload = Payload.builder().text("Hello, world!").build();
        WebhookResponse response = WebhookResponse.builder()
                .code(HttpStatus.OK.value())
                .build();

        when(slack.send(webhook, payload)).thenReturn(response);

        assertDoesNotThrow(() -> slackClient.send(webhook, payload));

    }

    @Test
    @DisplayName("Should throw exception when payload execution failed")
    void testSendFailure() throws IOException {
        String webhook = "https://hooks.slack.com/services/...";
        Payload payload = Payload.builder().text("Hello, world!").build();
        WebhookResponse response = WebhookResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        when(slack.send(webhook, payload)).thenReturn(response);

        var exception = assertThrows(RuntimeException.class, () -> slackClient.send(webhook, payload));
        assertEquals("Error on slack call: 500: null", exception.getMessage());
    }
}