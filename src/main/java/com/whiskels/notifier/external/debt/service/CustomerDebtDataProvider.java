package com.whiskels.notifier.external.debt.service;

import com.whiskels.notifier.external.DataLoaderAndProvider;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.moex.MoexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.debt.util.DebtUtil.*;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.customer.debt.url")
public class CustomerDebtDataProvider implements DataLoaderAndProvider<Debt> {
    @Value("${external.customer.debt.jsonNode:content}")
    private String jsonNode;
    @Value("${external.customer.debt.minRubValue:500}")
    private int minRubValue;
    @Value("${external.customer.debt.url}")
    private String customerUrl;
    private List<Debt> debts;
    private LocalDate lastUpdateDate;

    private final MoexService moexService;
    private final JsonReader jsonReader;
    private final Clock clock;

    @Override
    public List<Debt> get() {
        return debts;
    }

    @Override
    @PostConstruct
    @Scheduled(cron = "${external.customer.debt.cron:0 55 11 * * MON-FRI}", zone = "${common.timezone}")
    public void update() {
        log.info("Updating customer debt list");
        debts = filterAndSort(calculateTotalDebtFor(jsonReader.read(customerUrl, jsonNode, Debt.class)),
                totalDebtRoubleHigherThan(minRubValue));
        lastUpdateDate = now(clock);
    }

    @Override
    public LocalDate lastUpdate() {
        return lastUpdateDate;
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
