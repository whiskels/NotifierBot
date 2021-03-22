package com.whiskels.notifier.external.debt.service;

import com.whiskels.notifier.common.JsonUtil;
import com.whiskels.notifier.common.ReportBuilder;
import com.whiskels.notifier.external.AbstractJSONService;
import com.whiskels.notifier.external.DailyReport;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.external.moex.MoexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.debt.util.DebtUtil.*;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDebtService extends AbstractJSONService implements DailyReport<Debt> {
    private static final int MIN_RUB_VALUE = 500;
    private static final String DEBT_REPORT_HEADER = "Overdue debts";
    private static final String JSON_NODE = "content";

    @Value("${json.customer.debt.url}")
    private String customerUrl;

    private List<Debt> debts;

    private final MoexService moexService;
    private final Clock clock;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    @Scheduled(cron = "${json.customer.debt.cron}")
    protected void update() {
        moexService.update();
        updateCustomerList();
    }

    public String dailyReport(Predicate<Debt> predicate) {
        log.debug("Preparing customer debts message");

        return ReportBuilder.withHeader(DEBT_REPORT_HEADER, now(clock))
                .list(debts, predicate, COLLECTOR_EMPTY_LINE)
                .build();
    }

    private void updateCustomerList() {
        log.info("updating customer debt list");
        debts = filterAndSort(calculateTotalDebtFor(readFromJson(customerUrl)),
                totalDebtRoubleHigherThan(MIN_RUB_VALUE));
    }

    private List<Debt> readFromJson(String url) {
        return JsonUtil.readValuesFromNode(url, Debt.class, JSON_NODE);
    }

    private List<Debt> calculateTotalDebtFor(List<Debt> debtList) {
        final double usdRate = moexService.getUsdRate();
        final double eurRate = moexService.getEurRate();
        debtList.forEach(debt -> {
            debt.setTotalDebt(calculateTotalDebt(debt));
            debt.setTotalDebtRouble(calculateTotalDebtRouble(debt, usdRate, eurRate));
        });

        return debtList;
    }
}
