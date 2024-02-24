package com.whiskels.notifier.reporting.service.customer.debt.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.infrastructure.mock.MockUtil.read;

@Service
@Profile("mock")
@Slf4j
@RequiredArgsConstructor
class CustomerDebtFetchServiceMock implements DataFetchService<CustomerDebt> {
    private static final List<CustomerDebt> MOCKED_DATA = read("mocks/debt.json", new TypeReference<>() {
    });
    private final Clock clock;

    @NotNull
    @Override
    public ReportData<CustomerDebt> fetch() {
        log.warn("Returning mocked customer debt");
        return new ReportData<>(MOCKED_DATA, LocalDate.now(clock));
    }
}
