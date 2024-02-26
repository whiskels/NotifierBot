package com.whiskels.notifier.reporting.service.cleaner;

import com.whiskels.notifier.infrastructure.repository.AbstractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static com.whiskels.notifier.utilities.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;

@Slf4j
@Component
class DatabaseCleaner {
    private final List<AbstractRepository<?>> repositories;
    private final int workingDaysToDeleteAfter;
    private final Clock clock;

    public DatabaseCleaner(List<AbstractRepository<?>> repositories,
                           @Value("${database.cleaner.working-days-to-delete-after:14}") int workingDaysToDeleteAfter,
                           Clock clock) {
        this.repositories = repositories;
        this.workingDaysToDeleteAfter = workingDaysToDeleteAfter;
        this.clock = clock;
    }

    @Scheduled(cron = "${database.cleaner.cron:0 0 9 * * *}", zone = "${common.timezone}")
    void deleteOldEntries() {
        LocalDateTime deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter).atStartOfDay();
        repositories.forEach(repository -> {
            int deletedCount = repository.deleteByDateBefore(deleteBeforeDate);
            log.info("{} Deleted {} old entries loaded before {}",
                    AopProxyUtils.proxiedUserInterfaces(repository)[0].getSimpleName(), deletedCount, deleteBeforeDate);
        });
    }
}
