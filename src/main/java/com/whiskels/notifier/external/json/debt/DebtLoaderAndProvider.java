package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.json.InMemoryJsonDataLoaderAndProvider;
import com.whiskels.notifier.external.moex.MoexService;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.debt.DebtUtil.*;

@Component
@ConditionalOnProperty("external.customer.debt.url")
public class DebtLoaderAndProvider extends InMemoryJsonDataLoaderAndProvider<Debt> implements TelegramLabeled {
    @Value("${external.customer.debt.minRubValue:500}")
    private int minRubValue;
    @Value("${external.customer.debt.telegram.label:Debt loader}")
    private String telegramLabel;

    private final MoexService moexService;

    public DebtLoaderAndProvider(@Value("${external.customer.debt.url}") String jsonUrl,
                                 @Value("${external.customer.debt.json-node:content}") String jsonNode,
                                 MoexService moexService) {
        super(jsonUrl, jsonNode);
        this.moexService = moexService;
    }

    @Scheduled(cron = "${external.customer.debt.cron:0 55 11 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    protected List<Debt> loadData() {
        return filterAndSort(calculateTotalDebtFor(loadFromJson()),
                totalDebtRoubleHigherThan(minRubValue));
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

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
