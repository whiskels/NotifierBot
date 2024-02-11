package com.whiskels.notifier.reporting.service.customer.birthday.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
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
class CustomerBirthdayInfoFetchServiceMock implements DataFetchService<CustomerBirthdayInfo> {
    private static final List<CustomerBirthdayInfo> MOCKED_DATA = read("mocks/customer.json", new TypeReference<>() {
    });
    private final Clock clock;

    @NotNull
    @Override
    public ReportData<CustomerBirthdayInfo> fetch() {
        log.warn("Returning mocked customer birthday info");
        return new ReportData<>(MOCKED_DATA, LocalDate.now(clock));
    }
}
