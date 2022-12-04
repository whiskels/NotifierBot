package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.audit.Audit;
import com.whiskels.notifier.external.json.currency.CurrencyRate;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.LoaderType.DEBT;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
class DebtLoader implements Loader<Debt> {
    private final ReportSupplier<CurrencyRate> rateReportSupplier;
    private final DebtFeignClient debtClient;
    private static final int MIN_RUB_VALUE = 500;

    @Override
    @Audit(loader = DEBT)
    public List<Debt> load() {
        return ofNullable(debtClient.get())
                .map(DebtData::getContent)
                .map(this::calculateTotalDebt)
                .map(debts -> filterAndSort(debts, totalDebtRoubleHigherThan(MIN_RUB_VALUE)))
                .orElse(emptyList());
    }

    private List<Debt> calculateTotalDebt(List<Debt> debts) {
        List<CurrencyRate> currencyRates = rateReportSupplier.get().getContent();
        var currencyRate = currencyRates.isEmpty() ? null : currencyRates.get(0);
        debts.forEach(debt -> {
            debt.calculateTotal();
            debt.calculateTotalRouble(currencyRate);
        });
        return debts;
    }

    private static Predicate<Debt> totalDebtRoubleHigherThan(int amount) {
        return debt -> debt.getTotalRouble().compareTo(valueOf(amount)) > 0;
    }
}
