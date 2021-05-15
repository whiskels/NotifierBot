package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.dto.PaymentDto;
import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.util.StreamUtil.map;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = DataLoader.class)
public class PaymentDataProvider implements DataProvider<PaymentDto> {
    private final FinOperationRepository repository;
    private final DataLoader<FinancialOperation> dataLoader;

    public List<PaymentDto> get() {
        return map(getDataFromDb(), PaymentDto::fromEntity);
    }

    public LocalDate lastUpdate() {
        return dataLoader.lastUpdate();
    }

    private List<FinancialOperation> getDataFromDb() {
        return repository.findAll(where(loadDate(dataLoader.lastUpdate())
                .and(category(DB_CATEGORY_PAYMENT))), SORT_AMOUNT_RUB_DESC);
    }
}
