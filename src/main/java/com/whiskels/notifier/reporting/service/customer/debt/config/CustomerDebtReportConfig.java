package com.whiskels.notifier.reporting.service.customer.debt.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.debt.convert.CustomerDebtReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_DEBT;

@Configuration
@ConditionalOnBean(value = CustomerDebt.class, parameterizedContainer = DataFetchService.class)
public class CustomerDebtReportConfig {
    @Bean
    ReportMessageConverter<CustomerDebt> customerDebtReportMessageConverter(
            @Value("${report.parameters.customer-debt.header:\uD83E\uDDFE Debt report on}") String header,
            @Value("${report.parameters.customer-debt.no-data:Nobody}") String noData
    ) {
        return new CustomerDebtReportMessageConverter(header, noData);
    }

    @Bean
    GenericReportService<CustomerDebt> customerDebtReportRequestProcessor(
            DataFetchService<CustomerDebt> dataFetchService,
            ReportMessageConverter<CustomerDebt> messageCreator
    ) {
        return new GenericReportService<>(CUSTOMER_DEBT, dataFetchService, messageCreator);
    }
}
