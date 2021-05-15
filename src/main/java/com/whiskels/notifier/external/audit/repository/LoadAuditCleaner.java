package com.whiskels.notifier.external.audit.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.LocalDate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;

@Slf4j
@ComponentScan
@ConditionalOnBean(LoadAuditRepository.class)
@RequiredArgsConstructor
public class LoadAuditCleaner {
    @Value("${external.audit.workingDaysToDeleteAfter:14}")
    private int workingDaysToDeleteAfter;

    private final LoadAuditRepository repository;
    private final Clock clock;

    @Scheduled(cron = "${external.audit.cleanerCron:0 0 9 * * *}", zone = "${common.timezone}")
    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        log.info("Deleted {} old entries with load date before {}",
                repository.deleteByDateBefore(deleteBeforeDate),
                deleteBeforeDate);
    }
}
