package com.whiskels.notifier.reporting.service.customer.payment.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
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
class CustomerPaymentDtoFetchMock implements DataFetchService<CustomerPaymentDto> {
    private static final List<CustomerPaymentDto> MOCKED_DATA = read("mocks/payment.json", new TypeReference<>() {
    });
    private final Clock clock;

    @NotNull
    @Override
    public ReportData<CustomerPaymentDto> fetch() {
        log.warn("Returning mocked customer payment");
        return new ReportData<>(MOCKED_DATA, LocalDate.now(clock));
    }
}
