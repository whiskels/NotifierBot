package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.dto.PaymentDto;
import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.map;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = DataLoader.class)
public class PaymentDataProvider implements DataProvider<PaymentDto> {
    private final FinOperationRepository repository;
    private final DataLoader<FinancialOperation> dataLoader;

    public List<PaymentDto> getData() {
        return map(getDataFromDb(), PaymentDto::fromEntity);
    }

    public LocalDate lastUpdate() {
        return dataLoader.lastUpdate();
    }

    private List<FinancialOperation> getDataFromDb() {
        List<FinancialOperation> selectedOperations = repository.findAll(where(loadDate(dataLoader.lastUpdate())
                .and(category(DB_CATEGORY_PAYMENT))), SORT_AMOUNT_RUB_DESC);
        log.info("Selected {} financial operations from db", selectedOperations);
        return selectedOperations;
    }
}
