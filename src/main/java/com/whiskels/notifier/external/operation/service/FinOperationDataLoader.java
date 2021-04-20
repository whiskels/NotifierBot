package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.ExternalApiClient;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.moex.MoexService;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.*;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.customer.receivable.url")
public class FinOperationDataLoader implements ExternalApiClient<FinancialOperation> {
    @Value("${external.customer.receivable.workingDaysToLoad:2}")
    private int workingDaysToLoad;

    @Value("${external.customer.receivable.workingDaysToDeleteAfter:7}")
    private int workingDaysToDeleteAfter;

    @Value("${external.customer.receivable.url}")
    private String customerUrl;

    private final FinOperationRepository finOperationRepository;
    private final Clock clock;
    private final MoexService moexService;
    private final JsonReader jsonReader;

    @Scheduled(cron = "${external.customer.receivable.cron:0 55 11 * * MON-FRI}", zone = "${common.timezone}")
    public void update() {
        DayOfWeek today = now(clock).getDayOfWeek();
        if (today != SATURDAY && today != SUNDAY) {
            loadNewOperations();
            deleteOldEntries();
        }
    }

    private void loadNewOperations() {
        List<Integer> presentIds = finOperationRepository.getPresentCrmIdList();
        List<FinancialOperation> newFinancialOperations = filterAndSort(
                jsonReader.read(getNewUrl(), FinancialOperation.class), NEW_CRM_ID(presentIds));
        log.info("Found {} new receivables", newFinancialOperations.size());
        finOperationRepository.saveAll(calculateCurrencyAmount(newFinancialOperations));
    }

    private List<FinancialOperation> calculateCurrencyAmount(List<FinancialOperation> financialOperations) {
        final double usdRate = moexService.getUsdRate();
        final double eurRate = moexService.getEurRate();
        financialOperations.forEach(r -> {
            r.setAmountRub(calculateRoubleAmount(r, usdRate, eurRate));
            r.setAmountUsd(calculateUsdAmount(r, usdRate, eurRate));
        });

        return financialOperations;
    }

    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        log.info("Deleted {} old entries with load date before {}",
                finOperationRepository.deleteByDateBefore(deleteBeforeDate),
                deleteBeforeDate);
    }

    private String getNewUrl() {
        LocalDate endDate = now(clock).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
        log.info("Creating receivables url for range of: {} to {}", startDate, endDate);
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }
}
