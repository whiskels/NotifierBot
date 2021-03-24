package com.whiskels.notifier.external.receivable.service;

import com.whiskels.notifier.external.ExternalDataProvider;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.moex.MoexService;
import com.whiskels.notifier.external.receivable.domain.Receivable;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.external.receivable.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.FormatUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.NEW_CRM_ID;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.calculateRoubleAmount;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.customer.receivable.url")
public class ReceivableDataProvider implements ExternalDataProvider<ReceivableDto> {
    @Value("${external.customer.receivable.workingDaysToLoad:2}")
    private int workingDaysToLoad;

    @Value("${external.customer.receivable.workingDaysToDeleteAfter:7}")
    private int workingDaysToDeleteAfter;

    @Value("${external.customer.receivable.url}")
    private String customerUrl;

    private final ReceivableRepository receivableRepository;
    private final Clock clock;
    private final MoexService moexService;
    private final JsonReader jsonReader;

    @Override
    public List<ReceivableDto> get() {
        return receivableRepository.getRevenueByDate(now(clock));
    }

    @PostConstruct
    @Scheduled(cron = "${external.customer.receivable.cron}", zone = "${common.timezone}")
    public void update() {
        DayOfWeek today = now(clock).getDayOfWeek();
        if (today != SATURDAY && today != SUNDAY) {
            loadReceivables();
            deleteOldEntries();
        }
    }

    private void loadReceivables() {
        List<Integer> presentIds = receivableRepository.getIdList();
        List<Receivable> newReceivables = filterAndSort(
                jsonReader.read(getNewUrl(), Receivable.class), NEW_CRM_ID(presentIds));
        log.info("found {} new receivables", newReceivables.size());
        receivableRepository.saveAll(calculateRubAmount(newReceivables));
    }

    private List<Receivable> calculateRubAmount(List<Receivable> receivables) {
        final double usdRate = moexService.getUsdRate();
        final double eurRate = moexService.getEurRate();
        receivables.forEach(r -> r.setAmountRub(calculateRoubleAmount(r, usdRate, eurRate)));

        return receivables;
    }

    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        log.info("deleted {} old entries with load date before {}",
                receivableRepository.deleteByDateBefore(deleteBeforeDate),
                deleteBeforeDate);
    }

    private String getNewUrl() {
        LocalDate endDate = now(clock).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
        log.info("creating receivables url for range of: {} to {}", startDate, endDate);
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }
}
