package com.whiskels.notifier.service;

import com.whiskels.notifier.model.CustomerReceivable;
import com.whiskels.notifier.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.whiskels.notifier.model.CustomerReceivable.CATEGORY_REVENUE;
import static com.whiskels.notifier.util.DateTimeUtil.getWithOffset;
import static com.whiskels.notifier.util.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.util.FormatUtil.*;
import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerReceivableService extends AbstractJSONService {
    private static final int CACHED_DAYS = 2;
    private static final String REVENUE_REPORT_HEADER = "Revenue";

    @Value("${json.customer.receivable.url}")
    private String customerUrl;

    private List<CustomerReceivable> customerReceivables;

    // Storing data for n = CACHED_DAYS of previous days to show only unique receivables
    private List<List<CustomerReceivable>> customerReceivablesCache;

    @PostConstruct
    private void initCustomerList() {
        customerReceivablesCache = new ArrayList<>(CACHED_DAYS);
        IntStream.of(0, CACHED_DAYS - 1).forEach(i -> customerReceivablesCache.add(i, new ArrayList<>()));
        updateCustomerList();
    }

    @Scheduled(cron = "${json.customer.receivable.cron}")
    protected void update() {
        updateCache();
        updateCustomerList();
    }

    private void updateCustomerList() {
        log.info("updating receivable list");
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = subtractWorkingDays(endDate, CACHED_DAYS);
        final String actualUrl = getUrlBetween(startDate, endDate);

        customerReceivables = filterAndSort(readFromJson(actualUrl), List.of(isRevenue(), notCached()));
    }

    private void updateCache() {
        log.info("updating receivable cache");
        for (int i = CACHED_DAYS - 1; i >= 0; i--) {
            if (i != 0) {
                customerReceivablesCache.set(i, customerReceivablesCache.get(i - 1));
            } else {
                customerReceivablesCache.set(i, customerReceivables);
            }
        }
    }

    private String getUrlBetween(LocalDate startDate, LocalDate endDate) {
        return customerUrl.replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
    }

    private List<CustomerReceivable> readFromJson(String url) {
        return JsonUtil.readValuesFromUrl(url, CustomerReceivable.class);
    }

    public String dailyMessage() {
        return dailyMessage(alwaysTruePredicate());
    }

    public String dailyMessage(Predicate<CustomerReceivable> predicate) {
        log.debug("Preparing customer receivable message");

        return reportHeader(REVENUE_REPORT_HEADER, getWithOffset(serverHourOffset)) +
                formatList(customerReceivables, predicate);
    }

    private Predicate<CustomerReceivable> isRevenue() {
        return c -> c.getCategory().equalsIgnoreCase(CATEGORY_REVENUE);
    }

    private Predicate<CustomerReceivable> notCached() {
        return c -> customerReceivablesCache.stream().noneMatch(cache -> cache.contains(c));
    }
}
