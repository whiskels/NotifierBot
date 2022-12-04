package com.whiskels.notifier.external.json.currency;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.MemoizingReportSupplier;
import com.whiskels.notifier.external.ReportSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static java.util.Collections.singletonList;

@Configuration
class _CurrencyRateBeanConfig {
    @Bean
    public Loader<CurrencyRate> currencyRateLoader(CurrencyRateFeignClient currencyRateClient) {
        return () -> singletonList(currencyRateClient.get());
    }

    @Bean
    public ReportSupplier<CurrencyRate> currencyRateSupplier(Clock clock, Loader<CurrencyRate> loader) {
        return new MemoizingReportSupplier<>(loader, clock);
    }
}
