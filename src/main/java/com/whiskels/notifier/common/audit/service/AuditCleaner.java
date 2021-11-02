package com.whiskels.notifier.common.audit.service;

import com.whiskels.notifier.common.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditCleaner {
    @Value("${external.audit.workingDaysToDeleteAfter:14}")
    private int workingDaysToDeleteAfter;

    @SuppressWarnings("rawtypes")
    private final List<AuditRepository> auditRepositories;
    private final Clock clock;

    @Scheduled(cron = "${external.audit.cleanerCron:0 0 9 * * *}", zone = "${common.timezone}")
    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        auditRepositories.forEach(repository -> {
            int deletedAuditEntriesCount = repository.deleteByDateBefore(deleteBeforeDate);
            log.info("{} Deleted {} old entries with load date before {}",
                    repository.getClass().getSimpleName(), deletedAuditEntriesCount, deleteBeforeDate);
        });
    }
}
