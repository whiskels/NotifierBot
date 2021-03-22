package com.whiskels.notifier.external.receivable.service;

import com.whiskels.notifier.common.JsonUtil;
import com.whiskels.notifier.common.ReportBuilder;
import com.whiskels.notifier.external.AbstractJSONService;
import com.whiskels.notifier.external.DailyReport;
import com.whiskels.notifier.external.receivable.domain.Receivable;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.external.receivable.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.common.FormatUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.CATEGORY_REVENUE;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.NEW_CRM_ID;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceivableService extends AbstractJSONService implements DailyReport<ReceivableDto> {
    @Value("${json.customer.receivable.workingDaysToLoad:2}")
    private int workingDaysToLoad;

    @Value("${json.customer.receivable.workingDaysToDeleteAfter:7}")
    private int workingDaysToDeleteAfter;

    @Value("${json.customer.receivable.url}")
    private String customerUrl;

    private final ReceivableRepository receivableRepository;
    private final Clock clock;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    @Scheduled(cron = "${json.customer.receivable.cron}")
    protected void update() {
        loadReceivables();
        deleteOldEntries();
    }

    private void loadReceivables() {
        LocalDate endDate = now(clock).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
        log.info("loading receivables for range of: {} to {}", startDate, endDate);
        final String actualUrl = getUrlBetween(startDate, endDate);
        List<Integer> presentIds = receivableRepository.getIdList();
        List<Receivable> newReceivables = filterAndSort(readFromJson(actualUrl), NEW_CRM_ID(presentIds));
        log.info("found {} new receivables", newReceivables.size());
        receivableRepository.saveAll(newReceivables);
    }

    private void deleteOldEntries() {
        LocalDate deleteBeforeDate = subtractWorkingDays(now(clock), workingDaysToDeleteAfter);
        log.info("deleted {} old entries with load date before {}",
                receivableRepository.deleteByDateBefore(deleteBeforeDate),
                deleteBeforeDate);
    }

    private String getUrlBetween(LocalDate startDate, LocalDate endDate) {
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }

    private List<Receivable> readFromJson(String url) {
        return JsonUtil.readValuesFromUrl(url, Receivable.class);
    }

    public String dailyReport(Predicate<ReceivableDto> predicate) {
        log.debug("Preparing customer receivable message");
        final LocalDate reportDate = now(clock);

        return ReportBuilder.withHeader(CATEGORY_REVENUE, reportDate)
                .list(receivableRepository.getRevenueByDate(reportDate),
                        predicate, COLLECTOR_NEW_LINE)
                .build();
    }
}
