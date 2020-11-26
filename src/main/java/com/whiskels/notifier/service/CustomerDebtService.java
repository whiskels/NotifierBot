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
import java.util.stream.Collectors;

import static com.whiskels.notifier.util.DateTimeUtil.todayWithOffset;
import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDebtService extends AbstractJSONService {
    private static final int MIN_RUB_VALUE = 500;
    private static final String DEBT_REPORT_HEADER = "Overdue debts";

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

    public String dailyMessage(Predicate<CustomerDebt> predicate) {
        log.debug("Preparing customer debts message");

        return ReportBuilder.withHeader(DEBT_REPORT_HEADER, todayWithOffset(serverHourOffset))
                .listWithLine(customerDebts, predicate)
                .build();
    }

    public String dailyMessage() {
        return dailyMessage(alwaysTruePredicate());
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    private void updateCustomerList() {
        log.info("updating customer debt list");
        customerDebts = filterByMinRubValue(readFromJson(customerUrl));
    }

    private List<CustomerDebt> readFromJson(String url) {
        return JsonUtil.readValuesFromNode(url, CustomerDebt.class, "content");
    }

    private List<CustomerDebt> filterByMinRubValue(List<CustomerDebt> customerDebtList) {
        customerDebtList.forEach(customerDebt -> customerDebt.calculateOverallDebt(
                moexService.getUsdRate(), moexService.getEurRate()));

        return customerDebtList.stream()
                .filter(totalDebtRoubleHigherThan(MIN_RUB_VALUE))
                .sorted()
                .collect(Collectors.toList());
    }

    private Predicate<CustomerDebt> totalDebtRoubleHigherThan(int amount) {
        return customerDebt -> customerDebt.getTotalDebtRouble() > amount;
    }
}
