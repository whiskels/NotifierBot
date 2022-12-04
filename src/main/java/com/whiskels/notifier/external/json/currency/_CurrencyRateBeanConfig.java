package com.whiskels.notifier.external.json.currency;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.MemoizingReportSupplier;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.audit.Audit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

import static com.whiskels.notifier.external.LoaderType.CURRENCY_RATE;
import static java.util.Collections.singletonList;

@Configuration
class _CurrencyRateBeanConfig {
    @Bean
    public Loader<CurrencyRate> currencyRateLoader(CurrencyRateFeignClient currencyRateClient) {
        return new Loader<CurrencyRate>() {
            @Override
            @Audit(loader = CURRENCY_RATE)
            public List<CurrencyRate> load() {
                return singletonList(currencyRateClient.get());
                }
            };
    }

    @Bean
    public ReportSupplier<CurrencyRate> currencyRateSupplier(Clock clock, Loader<CurrencyRate> loader) {
        return new MemoizingReportSupplier<>(loader, clock);
    }
}
