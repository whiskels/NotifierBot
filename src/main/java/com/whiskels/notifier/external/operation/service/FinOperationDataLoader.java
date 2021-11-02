package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import com.whiskels.notifier.telegram.TelegramLabeled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.newCrmId;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.customer.operation.url")
public class FinOperationDataLoader implements DataLoader<FinancialOperation>, TelegramLabeled {
    @Value("${external.customer.operation.workingDaysToLoad:2}")
    private int workingDaysToLoad;

    @Value("${external.customer.operation.url}")
    private String customerUrl;

    @Value("${external.customer.operation.telegram.label:Financial operations}")
    private String telegramLabel;

    private final FinOperationRepository finOperationRepository;
    private final JsonReader jsonReader;
    private final Clock clock;
    private final FinOperationDataLoaderAuditor auditor;

    @Scheduled(cron = "${external.customer.operation.cron:0 5 12 * * MON-FRI}", zone = "${common.timezone}")
    public List<FinancialOperation> update() {
        DayOfWeek today = now(clock).getDayOfWeek();
        if (today != SATURDAY && today != SUNDAY) {
            List<FinancialOperation> newOperations = getNewFinancialOperations();
            save(newOperations);
            return newOperations;
        } else {
            return emptyList();
        }
    }

    @Override
    public LocalDate lastUpdate() {
        return auditor.lastUpdate();
    }

    private List<FinancialOperation> getNewFinancialOperations() {
        log.info("Preparing to check for new financial operations");
        List<FinancialOperation> requestedOperations = jsonReader.read(getNewUrl(), FinancialOperation.class);
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
        if (!operations.isEmpty()) {
            List<FinancialOperation> savedOperations =  finOperationRepository.saveAll(operations);
            log.info("Saved {} financial operations", savedOperations);
        }
        auditor.saveLoadAuditResults(operations);
    }


    private String getNewUrl() {
        LocalDate endDate = now(clock).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
        log.info("Creating receivables url for range of: {} to {}", startDate, endDate);
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
