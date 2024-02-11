package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinOperationDataFetchServiceTest {
    @Mock
    private FinOperationRepository finOperationRepository;
    @Mock
    private FinOperationFeignClient finOperationClient;

    private FinOperationDataFetchService service;

    @BeforeEach
    void initService() {
        service = new FinOperationDataFetchService(finOperationRepository, CLOCK, finOperationClient);
    }

    @DisplayName("Should not fetch data on weekend")
    @ParameterizedTest
    @ValueSource(strings = {"24", "25"})
    void shouldNotFetchDataOnWeekend(String day) {
        Clock mock = Clock.fixed(Instant.parse(STR."2024-02-\{day}T10:15:30Z"), ZoneId.of("UTC"));

        service = new FinOperationDataFetchService(finOperationRepository, mock, finOperationClient);

        service.fetch();

        verifyNoInteractions(finOperationClient);
        verifyNoInteractions(finOperationRepository);
    }

    @Test
    @DisplayName("Should fetch new financial operations data")
    void shouldFetchNewFinancialOperations() {
        when(finOperationRepository.getPresentCrmIds()).thenReturn(singleton(1));
        when(finOperationClient.get()).thenReturn(List.of(mockedOperation(1), mockedOperation(2)));
        when(finOperationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service = new FinOperationDataFetchService(finOperationRepository, CLOCK, finOperationClient);

        var actual = service.fetch();

        assertEquals(EXPECTED_DATE, actual.requestDate());
        assertEquals(1, actual.data().size());
    }

    private FinancialOperation mockedOperation(Integer crmId) {
        var operation = new FinancialOperation();
        operation.setCrmId(crmId);
        return operation;
    }

}