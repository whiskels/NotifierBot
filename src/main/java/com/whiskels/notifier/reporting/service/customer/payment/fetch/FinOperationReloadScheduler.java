package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentReportConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class FinOperationReloadScheduler {
    private final DataFetchService<FinancialOperation> dataFetchService;

    @Scheduled(cron = "${" + CustomerPaymentReportConfig.PAYMENT_PROPERTIES_PREFIX + ".cron:0 5 12 * * MON-FRI}", zone = "${common.timezone}")
    public void loadScheduled() {
        dataFetchService.fetch();
    }
}
