package com.whiskels.notifier.external.json.operation;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.audit.LoadAuditRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.FIN_OPERATION_URL;

@Configuration
@ConditionalOnProperty(FIN_OPERATION_URL)
class _FinOperationBeanConfig {
    static final String PROPERTIES_PREFIX = "external.customer.operation";
    static final String FIN_OPERATION_URL = PROPERTIES_PREFIX + ".url";

    @Bean
    Loader<FinancialOperation> financialOperationLoader(
            FinOperationRepository repository,
            Clock clock,
            FinOperationFeignClient finOperationFeignClient
    ) {
        return new FinOperationLoader(repository, clock, finOperationFeignClient);
    }

    @Bean
    FinOperationScheduler finOperationScheduler(Loader<FinancialOperation> loader) {
        return new FinOperationScheduler(loader);
    }

    @Bean
    ReportSupplier<PaymentDto> paymentDtoSupplier(
            FinOperationRepository repository,
            LoadAuditRepository auditRepository
    ) {
        return new PaymentReportSupplier(repository, auditRepository);
    }
}
