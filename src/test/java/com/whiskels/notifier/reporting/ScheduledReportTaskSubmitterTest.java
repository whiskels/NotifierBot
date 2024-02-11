package com.whiskels.notifier.reporting;

import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.ScheduledReportTaskSubmitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_DEBT;
import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ScheduledReportTaskSubmitterTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ReportServiceImpl reportService;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Captor
    private ArgumentCaptor<CronTrigger> cronTriggerCaptor;

    private static Map<ReportType, String> cronMap() {
        return Map.of(
                EMPLOYEE_EVENT, "0 0 12 * * ?",
                CUSTOMER_DEBT, "0 0 18 * * ?"
        );
    }

    @Test
    @DisplayName("Should trigger on application ready event")
    void shouldTriggerOnApplicationReadyEvent() {
        ScheduledReportTaskSubmitter taskSubmitter = new ScheduledReportTaskSubmitter(taskScheduler, reportService, cronMap(), "UTC");
        taskSubmitter.onApplicationEvent(mock(ApplicationReadyEvent.class));
        verify(taskScheduler, times(2)).schedule(runnableCaptor.capture(), cronTriggerCaptor.capture());

        assertThat(cronTriggerCaptor.getAllValues())
                .containsExactlyInAnyOrder(
                new CronTrigger("0 0 12 * * ?", TimeZone.getTimeZone(ZoneId.of("UTC"))),
                new CronTrigger("0 0 18 * * ?", TimeZone.getTimeZone(ZoneId.of("UTC"))));

        runnableCaptor.getValue().run();
        verify(reportService).executeReport(any(ReportType.class));
    }

    @Test
    @DisplayName("Should do nothing when cron map is not configured")
    void shouldDoNothingWhenCronMapIsNotConfigured() {
        ScheduledReportTaskSubmitter taskSubmitter = new ScheduledReportTaskSubmitter(taskScheduler, reportService, Collections.emptyMap(), "UTC");
        taskSubmitter.onApplicationEvent(mock(ApplicationReadyEvent.class));
        verifyNoInteractions(taskScheduler);
    }
}