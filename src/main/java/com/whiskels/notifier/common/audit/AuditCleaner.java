package com.whiskels.notifier.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;

@Slf4j
@Component
class AuditCleaner {
    private final List<AuditRepository<?>> repositories;
    private final int workingDaysToDeleteAfter;
    private final Clock clock;

    public AuditCleaner(List<AuditRepository<?>> repositories,
                        @Value("${external.audit.working-days-to-delete-after:14}") int workingDaysToDeleteAfter,
                        Clock clock) {
        this.repositories = repositories;
        this.workingDaysToDeleteAfter = workingDaysToDeleteAfter;
        this.clock = clock;
    }

    @Scheduled(cron = "${external.audit.cleaner-cron:0 0 9 * * *}", zone = "${common.timezone}")
    public void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        repositories.forEach(repository -> {
            int deletedCount = repository.deleteByDateBefore(deleteBeforeDate);
            log.info("{} Deleted {} old entries with load date before {}",
                    repository.getLabel(), deletedCount, deleteBeforeDate);
        });
    }
}
