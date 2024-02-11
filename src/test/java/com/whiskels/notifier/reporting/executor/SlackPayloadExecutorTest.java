package com.whiskels.notifier.reporting.executor;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.slack.SlackClient;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.SlackPayloadExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_BIRTHDAY;
import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlackPayloadExecutorTest {
    private static final Payload DUMMY_PAYLOAD = Payload.builder().build();

    @Mock
    private SlackClient slackClient;

    private SlackPayloadExecutor slackPayloadExecutor;

    @Test
    @DisplayName("Should send when type exists")
    void shouldSentWhenTypeExists() throws IOException {
        Map<ReportType, String> webhookMappings = Map.of(EMPLOYEE_EVENT, "https://example.com/some-webhook");
        slackPayloadExecutor = new SlackPayloadExecutor(slackClient, webhookMappings);

        slackPayloadExecutor.send(EMPLOYEE_EVENT, DUMMY_PAYLOAD);

        verify(slackClient).send(webhookMappings.get(EMPLOYEE_EVENT), DUMMY_PAYLOAD);
    }

    @Test
    @DisplayName("Should throw exception when type does not exist")
    void shouldThrowExceptionWhenTypeDoesNotExist() throws IOException {
        slackPayloadExecutor = new SlackPayloadExecutor(slackClient, emptyMap());

        assertThrows(IllegalStateException.class, () -> {
            slackPayloadExecutor.send(CUSTOMER_BIRTHDAY, DUMMY_PAYLOAD);
        });

        verify(slackClient, never()).send(anyString(), any(Payload.class));
    }
}