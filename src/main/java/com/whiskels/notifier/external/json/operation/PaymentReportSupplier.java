package com.whiskels.notifier.external.json.operation;

import com.whiskels.notifier.external.ReportData;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.audit.LoadAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.map;
import static com.whiskels.notifier.external.LoaderType.FINANCIAL_OPERATION;
import static com.whiskels.notifier.external.json.operation.FinOperationUtil.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@RequiredArgsConstructor
class PaymentReportSupplier implements ReportSupplier<PaymentDto> {
    private final FinOperationRepository repository;
    private final LoadAuditRepository auditRepository;

    public ReportData<PaymentDto> get() {
        LocalDate lastUpdate = auditRepository.getLastUpdateDate(FINANCIAL_OPERATION);
        List<FinancialOperation> selectedOperations = repository.findAll(where(loadDate(lastUpdate)
                .and(category(DB_CATEGORY_PAYMENT))), SORT_AMOUNT_RUB_DESC);
        log.info("Selected {} financial operations from db", selectedOperations.size());
        return new ReportData<>(map(selectedOperations, PaymentDto::from), lastUpdate);
    }
}
