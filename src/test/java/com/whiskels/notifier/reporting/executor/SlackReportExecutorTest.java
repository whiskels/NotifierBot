package com.whiskels.notifier.reporting.executor;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.infrastructure.report.slack.SlackClient;
import com.whiskels.notifier.infrastructure.report.slack.SlackPayloadMapper;
import com.whiskels.notifier.infrastructure.report.slack.SlackReportExecutor;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.Report;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlackReportExecutorTest {
    private static final Payload DUMMY_PAYLOAD = Payload.builder().build();

    @Mock
    private SlackClient client;
    @Mock
    private SlackPayloadMapper mapper;

    private SlackReportExecutor executor;

    @Test
    @DisplayName("Should send when type exists")
    void shouldSentWhenTypeExists() throws IOException {
        Map<ReportType, String> webhookMappings = Map.of(EMPLOYEE_EVENT, "https://example.com/some-webhook");
        executor = new SlackReportExecutor(client, mapper, webhookMappings);
        given(mapper.map(any())).willReturn(DUMMY_PAYLOAD);

        executor.send(EMPLOYEE_EVENT, Report.builder().build());

        verify(client).send(webhookMappings.get(EMPLOYEE_EVENT), DUMMY_PAYLOAD);
    }

    @Test
    @DisplayName("Should throw exception when type does not exist")
    void shouldTNothrowExceptionWhenTypeDoesNotExist() throws IOException {
        executor = new SlackReportExecutor(client, mapper, emptyMap());

        assertDoesNotThrow(() -> executor.send(CUSTOMER_BIRTHDAY, Report.builder().build()));

        verify(client, never()).send(anyString(), any(Payload.class));
    }
}