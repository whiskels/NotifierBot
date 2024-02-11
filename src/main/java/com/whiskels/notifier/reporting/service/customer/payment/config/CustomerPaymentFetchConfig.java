package com.whiskels.notifier.reporting.service.customer.payment.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationDataFetchService;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationFeignClient;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationReloadScheduler;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationRepository;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.PaymentReportDataFetchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentFetchConfig.PAYMENT_URL;
import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentReportConfig.PAYMENT_PROPERTIES_PREFIX;

@Configuration
@ConditionalOnProperty(PAYMENT_URL)
@EnableScheduling
public class CustomerPaymentFetchConfig {
    public static final String PAYMENT_URL = PAYMENT_PROPERTIES_PREFIX + ".url";

    @Bean
    DataFetchService<FinancialOperation> financialOperationDataFetchService(
            final FinOperationRepository finOperationRepository,
            final Clock clock,
            final FinOperationFeignClient finOperationClient
    ) {
        return new FinOperationDataFetchService(finOperationRepository, clock, finOperationClient);
    }

    @Bean
    FinOperationReloadScheduler finOperationReloadScheduler(DataFetchService<FinancialOperation> dataFetchService) {
        return new FinOperationReloadScheduler(dataFetchService);
    }

    @Bean
    DataFetchService<CustomerPaymentDto> customerPaymentDtoDataFetchService(
            final FinOperationRepository repository,
            final LoadAuditRepository auditRepository
    ) {
        return new PaymentReportDataFetchService(repository, auditRepository);
    }
}
