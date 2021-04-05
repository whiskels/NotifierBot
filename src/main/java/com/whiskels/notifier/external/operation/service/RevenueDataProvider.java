package com.whiskels.notifier.external.operation.service;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.ExternalApiClient;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import com.whiskels.notifier.external.operation.dto.FinancialOperationDto;
import com.whiskels.notifier.external.operation.repository.FinOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.common.StreamUtil.map;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(value = FinancialOperation.class, parameterizedContainer = ExternalApiClient.class)
public class RevenueDataProvider implements DataProvider<FinancialOperationDto> {
    private final FinOperationRepository repository;

    public List<FinancialOperationDto> get() {
        return map(getDataFromDb(), FinancialOperationDto::fromEntity);
    }

    public LocalDate lastUpdate() {
        return repository.lastUpdateDate();
    }

    private List<FinancialOperation> getDataFromDb() {
        return repository.findAll(where(loadDate(repository.lastUpdateDate())
                .and(category(DB_CATEGORY_PAYMENT))), SORT_AMOUNT_RUB_DESC);
    }
}
