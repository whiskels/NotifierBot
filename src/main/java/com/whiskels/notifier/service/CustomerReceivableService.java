package com.whiskels.notifier.service;

import com.whiskels.notifier.model.CustomerReceivable;
import com.whiskels.notifier.util.CacheService;
import com.whiskels.notifier.util.JsonUtil;
import com.whiskels.notifier.util.ReportBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.model.CustomerReceivable.CATEGORY_REVENUE;
import static com.whiskels.notifier.util.CustomerReceivableUtil.IS_REVENUE;
import static com.whiskels.notifier.util.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.util.DateTimeUtil.todayWithOffset;
import static com.whiskels.notifier.util.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.util.FormatUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerReceivableService extends AbstractJSONService implements DailyReport<CustomerReceivable> {
    private static final int CACHED_DAYS = 2;

    @Value("${json.customer.receivable.url}")
    private String customerUrl;

    private CacheService<CustomerReceivable> cache;

    private List<CustomerReceivable> customerReceivables;

    @PostConstruct
    private void initCustomerList() {
        cache = new CacheService<>(CACHED_DAYS);
        updateCustomerList();
    }

    @Scheduled(cron = "${json.customer.receivable.cron}")
    protected void update() {
        updateCache();
        updateCustomerList();
    }

    private void updateCustomerList() {
        log.info("updating receivable list");
        LocalDate endDate = todayWithOffset(serverHourOffset).minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, CACHED_DAYS);
        final String actualUrl = getUrlBetween(startDate, endDate);

        customerReceivables = filterAndSort(readFromJson(actualUrl), IS_REVENUE, cache.notCached());
    }

    private void updateCache() {
        log.info("updating receivable cache");
        cache.updateCache(customerReceivables);
    }

    private String getUrlBetween(LocalDate startDate, LocalDate endDate) {
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }

    private List<CustomerReceivable> readFromJson(String url) {
        return JsonUtil.readValuesFromUrl(url, CustomerReceivable.class);
    }

    public String dailyReport(Predicate<CustomerReceivable> predicate) {
        log.debug("Preparing customer receivable message");

        return ReportBuilder.withHeader(CATEGORY_REVENUE, todayWithOffset(serverHourOffset))
                .list(customerReceivables, predicate, COLLECTOR_NEW_LINE)
                .build();
    }
}
