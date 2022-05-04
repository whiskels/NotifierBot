package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.JsonLoader;
import com.whiskels.notifier.external.moex.Currency;
import com.whiskels.notifier.external.moex.MoexRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.debt.DebtUtil.*;
import static com.whiskels.notifier.external.moex.Currency.EUR_RUB;
import static com.whiskels.notifier.external.moex.Currency.USD_RUB;

@Component
@ConditionalOnProperty("external.customer.debt.url")
public class DebtLoader extends JsonLoader<Debt> {
    private final int minRubValue;
    private final Supplier<MoexRate> rateSupplier;

    public DebtLoader(@Value("${external.customer.debt.url}") String jsonUrl,
                      @Value("${external.customer.debt.json-node:content}") String jsonNode,
                      @Value("${external.customer.debt.minRubValue:500}") int minRubValue,
                      Supplier<MoexRate> rateSupplier) {
        super(jsonUrl, jsonNode);
        this.minRubValue = minRubValue;
        this.rateSupplier = rateSupplier;
    }

    @Override
    public List<Debt> load() {
        return filterAndSort(calculateTotalDebtFor(loadFromJson()),
                totalDebtRoubleHigherThan(minRubValue));
    }

    private List<Debt> calculateTotalDebtFor(List<Debt> debtList) {
        List<MoexRate> moexRates = rateSupplier.getData();
        debtList.forEach(debt -> {
            debt.setTotalDebt(calculateTotalDebt(debt));
            debt.setTotalDebtRouble(calculateTotalDebtRouble(debt, getRate(moexRates, USD_RUB), getRate(moexRates, EUR_RUB)));
        });

        return debtList;
    }

    private double getRate(Collection<MoexRate> moexRates, Currency currency) {
        return moexRates.stream()
                .filter(rate -> rate.getCurrency().equals(currency))
                .map(MoexRate::getRate)
                .findAny()
                .orElse(currency.getDefaultValue());
    }
}
