package com.whiskels.notifier.external.json.operation.service;

import com.whiskels.notifier.external.json.JsonLoader;
import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.json.operation.repository.FinOperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.operation.util.FinOperationUtil.newCrmId;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Service
@Slf4j
@ConditionalOnProperty("external.customer.operation.url")
public class FinOperationLoader extends JsonLoader<FinancialOperation> {
    private final FinOperationRepository finOperationRepository;
    private final Clock clock;
    private final FinOperationDataLoaderAuditor auditor;

    @Value("${external.customer.operation.workingDaysToLoad:2}")
    private int workingDaysToLoad;

    @Autowired
    public FinOperationLoader(@Value("${external.customer.operation.url}") String jsonUrl,
                              FinOperationRepository finOperationRepository,
                              FinOperationDataLoaderAuditor auditor,
                              Clock clock) {
        super(jsonUrl);
        this.finOperationRepository = finOperationRepository;
        this.clock = clock;
        this.auditor = auditor;
    }

    @Override
    @Scheduled(cron = "${external.customer.operation.cron:0 5 12 * * MON-FRI}", zone = "${common.timezone}")
    public List<FinancialOperation> load() {
        DayOfWeek today = now(clock).getDayOfWeek();
        if (today != SATURDAY && today != SUNDAY) {
            List<FinancialOperation> newOperations = getNewFinancialOperations();
            save(newOperations);
            return newOperations;
        } else {
            return emptyList();
        }
    }

    private List<FinancialOperation> getNewFinancialOperations() {
        log.info("Preparing to check for new financial operations");
        List<FinancialOperation> requestedOperations = loadFromJson(getNewUrl());
        log.info("Request returned {} financial operations with crm ids: {}",
                requestedOperations.size(),
                requestedOperations.stream()
                        .map(FinancialOperation::getCrmId)
                        .map(String::valueOf)
                        .collect(COLLECTOR_COMMA_SEPARATED)
        );

        List<Integer> presentIds = finOperationRepository.getPresentCrmIdList();
        List<FinancialOperation> newOperations = filterAndSort(requestedOperations, newCrmId(presentIds));
        log.info("Found {} new financial operations", newOperations.size());
        return newOperations;
    }

    private void save(List<FinancialOperation> operations) {
        List<FinancialOperation> savedOperations = finOperationRepository.saveAll(operations);
        auditor.saveLoadAuditResults(operations);
        log.info("Saved {} financial operations", savedOperations);
    }


    private String getNewUrl() {
        LocalDate endDate = now(clock).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
        log.info("Creating receivables url for range of: {} to {}", startDate, endDate);
        return jsonUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }
}
