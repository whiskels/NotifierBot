package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.utilities.collections.StreamUtil.map;
import static java.time.LocalDate.now;

@Slf4j
@RequiredArgsConstructor
public class PaymentReportDataFetchService implements DataFetchService<CustomerPaymentDto> {
    private static final String DB_CATEGORY_PAYMENT = "Revenue";

    private final FinOperationRepository repository;
    private final LoadAuditRepository auditRepository;

    @Nonnull
    @Override
    public ReportData<CustomerPaymentDto> fetch() {
        return auditRepository.findLastUpdateDateTime(ReportType.CUSTOMER_PAYMENT)
                .map(dateTime -> {
                    List<FinancialOperation> selectedOperations =
                            repository.getAllByCategoryAndDateBetween(
                                    DB_CATEGORY_PAYMENT,
                                    dateTime.with(LocalTime.MIN),
                                    dateTime.with(LocalTime.MAX)
                            );
                    log.info("Selected {} payment operations from db", selectedOperations.size());
                    return new ReportData<>(map(selectedOperations, CustomerPaymentDto::from), dateTime.toLocalDate());
                })
                .orElseGet(() -> new ReportData<>(Collections.emptyList(), now()));
    }
}
