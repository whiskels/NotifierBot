package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.MemoizingReportSupplier;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.currency.CurrencyRate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static com.whiskels.notifier.external.json.debt._DebtConfig.DEBT_URL;

@Configuration
@ConditionalOnProperty(DEBT_URL)
class _DebtConfig {
    static final String DEBT_URL = "external.customer.debt.url";

    @Bean
    public Loader<Debt> debtLoader(ReportSupplier<CurrencyRate> currencyRateReportSupplier,
                                   DebtFeignClient debtClient) {
        return new DebtLoader(currencyRateReportSupplier, debtClient);
    }

    @Bean
    public ReportSupplier<DebtDto> debtDtoSupplier(Clock clock, Loader<Debt> loader) {
        ReportSupplier<Debt> reportSupplier = new MemoizingReportSupplier<>(loader, clock);
        return () -> reportSupplier.get().remap(DebtDto::from);
    }
}
