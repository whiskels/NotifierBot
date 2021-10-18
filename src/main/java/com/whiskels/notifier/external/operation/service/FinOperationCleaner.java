package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;

@Slf4j
@Component
@ConditionalOnBean(FinOperationDataLoader.class)
@RequiredArgsConstructor
public class FinOperationCleaner {
    @Value("${external.customer.operation.workingDaysToDeleteAfter:14}")
    private int workingDaysToDeleteAfter;

    private final FinOperationRepository repository;
    private final Clock clock;

    @Scheduled(cron = "${external.customer.operation.cleanerCron:0 0 9 * * *}", zone = "${common.timezone}")
    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        log.info("Deleted {} old entries with load date before {}",
                repository.deleteByDateBefore(deleteBeforeDate),
                deleteBeforeDate);
    }
}
