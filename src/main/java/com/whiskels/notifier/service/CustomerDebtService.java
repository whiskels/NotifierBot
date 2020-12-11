package com.whiskels.notifier.service;

import com.whiskels.notifier.model.CustomerDebt;
import com.whiskels.notifier.util.JsonUtil;
import com.whiskels.notifier.util.ReportBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.util.CustomerDebtUtil.calculateOverallDebt;
import static com.whiskels.notifier.util.CustomerDebtUtil.totalDebtRoubleHigherThan;
import static com.whiskels.notifier.util.DateTimeUtil.todayWithOffset;
import static com.whiskels.notifier.util.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDebtService extends AbstractJSONService implements DailyReport<CustomerDebt> {
    private static final int MIN_RUB_VALUE = 500;
    private static final String DEBT_REPORT_HEADER = "Overdue debts";
    private static final String JSON_NODE = "content";

    @Value("${json.customer.debt.url}")
    private String customerUrl;

    private List<CustomerDebt> customerDebts;

    private final MoexService moexService;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    @Scheduled(cron = "${json.customer.debt.cron}")
    protected void update() {
        moexService.update();
        updateCustomerList();
    }

    public String dailyReport(Predicate<CustomerDebt> predicate) {
        log.debug("Preparing customer debts message");

        return ReportBuilder.withHeader(DEBT_REPORT_HEADER, todayWithOffset(serverHourOffset))
                .list(customerDebts, predicate, COLLECTOR_EMPTY_LINE)
                .build();
    }

    private void updateCustomerList() {
        log.info("updating customer debt list");
        customerDebts = filterAndSort(calculateTotalDebt(readFromJson(customerUrl)),
                totalDebtRoubleHigherThan(MIN_RUB_VALUE));
    }

    private List<CustomerDebt> readFromJson(String url) {
        return JsonUtil.readValuesFromNode(url, CustomerDebt.class, JSON_NODE);
    }

    private List<CustomerDebt> calculateTotalDebt(List<CustomerDebt> customerDebtList) {
        final double usdRate = moexService.getUsdRate();
        final double eurRate = moexService.getEurRate();
        customerDebtList.forEach(customerDebt ->
                customerDebt.setTotalDebtRouble(calculateOverallDebt(customerDebt, usdRate, eurRate)));

        return customerDebtList;
    }
}
