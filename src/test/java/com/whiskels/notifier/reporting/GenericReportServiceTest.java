package com.whiskels.notifier.reporting;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericReportServiceTest {

    @Mock
    private DataFetchService<String> dataFetchService;

    @Mock
    private ReportMessageConverter<String> messageCreator;

    @InjectMocks
    private GenericReportService<String> genericReportService;

    @Test
    @DisplayName("Should prepare reports")
    void shouldPrepareReports() {
        var payload = Payload.builder().build();
        when(dataFetchService.fetch()).thenReturn(new ReportData<>(List.of("Data1", "Data2"), LocalDate.now()));
        when(messageCreator.convert(any())).thenReturn(List.of(payload));

        Iterable<Payload> payloads = genericReportService.prepareReports();
        List<Payload> payloadList = (List<Payload>) payloads;

        assertEquals(1, payloadList.size());
        assertEquals(payloadList.getFirst(), payload);
    }
}