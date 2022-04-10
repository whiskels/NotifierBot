package com.whiskels.notifier.external.json.operation.service;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.audit.repository.LoadAuditRepository;
import com.whiskels.notifier.external.json.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.json.operation.dto.PaymentDto;
import com.whiskels.notifier.external.json.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.map;
import static com.whiskels.notifier.external.audit.domain.Loader.FINANCIAL_OPERATION;
import static com.whiskels.notifier.external.json.operation.util.FinOperationUtil.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = Loader.class)
public class PaymentSupplier implements Supplier<PaymentDto> {
    private final FinOperationRepository repository;
    private final LoadAuditRepository auditRepository;
    private final Loader<FinancialOperation> loader;

    public List<PaymentDto> getData() {
        return map(getDataFromDb(), PaymentDto::fromEntity);
    }

    public LocalDate lastUpdate() {
        return auditRepository.getLastUpdateDate(FINANCIAL_OPERATION);
    }

    @Override
    public void update() {
        loader.load();
    }

    private List<FinancialOperation> getDataFromDb() {
        List<FinancialOperation> selectedOperations = repository.findAll(where(loadDate(lastUpdate())
                .and(category(DB_CATEGORY_PAYMENT))), SORT_AMOUNT_RUB_DESC);
        log.info("Selected {} financial operations from db", selectedOperations);
        return selectedOperations;
    }
}
