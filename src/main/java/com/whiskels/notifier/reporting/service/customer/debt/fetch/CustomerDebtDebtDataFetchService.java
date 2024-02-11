package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_DEBT;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class CustomerDebtDebtDataFetchService implements DataFetchService<CustomerDebt> {
    private static final int MIN_RUB_VALUE = 500;

    private final DataFetchService<CurrencyRate> rateReportSupplier;
    private final CustomerDebtFeignClient debtClient;
    private final Clock clock;

    @AuditDataFetchResult(reportType = CUSTOMER_DEBT)
    @Nonnull
    @Override
    public ReportData<CustomerDebt> fetch() {
        var currencyRate = getCurrencyRate();

        List<CustomerDebt> result = ofNullable(debtClient.get())
                .map(CustomerDebtData::getContent)
                .orElse(emptyList())
                .stream()
                .filter(debt -> {
                    calculateTotalDebt(debt, currencyRate);
                    return totalDebtRoubleHigherThan(MIN_RUB_VALUE).test(debt);
                })
                .sorted()
                .collect(Collectors.toList());

        return new ReportData<>(result, now(clock));
    }

    private CurrencyRate getCurrencyRate() {
        return Optional.of(rateReportSupplier.fetch())
                .map(ReportData::data)
                .map(list -> list.get(0))
                .orElse(null);
    }

    private void calculateTotalDebt(CustomerDebt customerDebt, CurrencyRate currencyRate) {
        customerDebt.calculateTotal();
        customerDebt.calculateTotalRouble(currencyRate);
    }

    private static Predicate<CustomerDebt> totalDebtRoubleHigherThan(int amount) {
        return debt -> debt.getTotalRouble().compareTo(valueOf(amount)) > 0;
    }
}
