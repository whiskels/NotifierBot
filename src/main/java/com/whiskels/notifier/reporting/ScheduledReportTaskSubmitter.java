package com.whiskels.notifier.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Nonnull;
import java.time.ZoneId;
import java.util.Map;
import java.util.TimeZone;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Slf4j
public class ScheduledReportTaskSubmitter implements ApplicationListener<ApplicationReadyEvent> {
    private final TaskScheduler executor;
    private final ReportService service;
    private final Map<ReportType, String> reportCronMap;
    private final String timeZone;

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        scheduleReports();
    }

    private void scheduleReports() {
        if (isEmpty(reportCronMap)) {
            log.error("No cron triggers found for reports");
            return;
        }
        reportCronMap.forEach((type, cron) ->
                        executor.schedule(() -> service.executeReport(type),
                                new CronTrigger(cron, TimeZone.getTimeZone(ZoneId.of(timeZone))
                                )
                        )
                );
    }
}