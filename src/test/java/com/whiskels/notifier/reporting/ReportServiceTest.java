package com.whiskels.notifier.reporting;

import com.whiskels.notifier.infrastructure.report.slack.SlackReportExecutor;
import com.whiskels.notifier.reporting.exception.ExceptionEvent;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_BIRTHDAY;
import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_DEBT;
import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    private static final Report MOCKED_REPORT = Report.builder().build();

    @Mock
    private GenericReportService<?> genericReportServiceOne;

    @Mock
    private GenericReportService<?> genericReportServiceTwo;

    @Mock
    private SlackReportExecutor payloadExecutor;

    @Mock
    private ApplicationEventPublisher publisher;

    private ReportServiceImpl reportService;

    @BeforeEach
    void initReportService() {
        lenient().when(genericReportServiceOne.getType()).thenReturn(EMPLOYEE_EVENT);
        lenient().when(genericReportServiceOne.prepareReports()).thenReturn(List.of(MOCKED_REPORT));
        lenient().when(genericReportServiceTwo.getType()).thenReturn(CUSTOMER_DEBT);
        lenient().when(genericReportServiceTwo.prepareReports()).thenThrow(new RuntimeException("Test exception"));
        reportService = new ReportServiceImpl(List.of(genericReportServiceOne, genericReportServiceTwo), List.of(payloadExecutor), publisher);
    }

    @Test
    @DisplayName("Should execute report successfully")
    void shouldExecuteReportSuccessfully() {
        ReportType type = EMPLOYEE_EVENT;

        reportService.executeReport(type);

        verify(payloadExecutor).send(type, MOCKED_REPORT);
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should throw exception when no processor found")
    void shouldThrowExceptionWhenNoProcessorFound() {
        assertThrows(RuntimeException.class, () -> reportService.executeReport(CUSTOMER_BIRTHDAY));
        verify(publisher).publishEvent(any(ExceptionEvent.class));
    }

    @Test
    @DisplayName("Should publish exception when processor throws")
    void shouldPublishExceptionWhenProcessorThrows() {
        assertThrows(RuntimeException.class, () -> reportService.executeReport(CUSTOMER_DEBT));

        verify(publisher).publishEvent(any(ExceptionEvent.class));
    }

    @Test
    @DisplayName("Should get report types")
    void shouldGetReportTypes() {
        List<ReportType> reportTypes = reportService.getReportTypes();

        assertThat(reportTypes).containsExactlyInAnyOrder(EMPLOYEE_EVENT, CUSTOMER_DEBT);
    }
}