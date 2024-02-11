package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CurrencyRateDataFetchService;
import com.whiskels.notifier.reporting.service.customer.debt.fetch.CurrencyRateFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateDataFetchServiceTest {

    @Mock
    private CurrencyRateFeignClient client;

    private CurrencyRateDataFetchService currencyRateDataFetchService;

    @Test
    @DisplayName("Should fetch report data")
    void shouldFetchReportData() {
    currencyRateDataFetchService = new CurrencyRateDataFetchService(client, MockedClockConfiguration.CLOCK);
        when(client.get()).thenReturn(new CurrencyRate());

        var actual = currencyRateDataFetchService.fetch();

        assertEquals(1, actual.data().size());
        assertEquals(MockedClockConfiguration.EXPECTED_DATE, actual.requestDate());
    }

    @Test
    @DisplayName("Should fetch report data when no data is present")
    void shouldFetchReportDataWhenNoDataPresent() {
        currencyRateDataFetchService = new CurrencyRateDataFetchService(client, MockedClockConfiguration.CLOCK);
        when(client.get()).thenReturn(null);

        var actual = currencyRateDataFetchService.fetch();

        assertEquals(0, actual.data().size());
        assertEquals(MockedClockConfiguration.EXPECTED_DATE, actual.requestDate());
    }
}