package com.whiskels.notifier.reporting.service.customer.debt.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.debt.convert.CustomerDebtReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CurrencyRateDataFetchService;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CurrencyRateFeignClient;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CustomerDebtDebtDataFetchService;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CustomerDebtFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_DEBT;

@Configuration
@ConditionalOnProperty(CustomerDebtFeignClient.DEBT_URL)
public class CustomerDebtFetchConfig {
    @Bean
    DataFetchService<CurrencyRate> currencyRateDataFetchService(
            final CurrencyRateFeignClient client,
            final Clock clock
    ) {
        return new CurrencyRateDataFetchService(client, clock);
    }

    @Bean
    @Primary
    DataFetchService<CustomerDebt> customerDebtDataFetchService(
            final DataFetchService<CurrencyRate> rateReportSupplier,
            final CustomerDebtFeignClient debtClient,
            final Clock clock
    ) {
        return new CustomerDebtDebtDataFetchService(rateReportSupplier, debtClient, clock);
    }
}
