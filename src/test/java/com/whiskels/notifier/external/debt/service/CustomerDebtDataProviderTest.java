package com.whiskels.notifier.external.debt.service;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.moex.MoexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static com.whiskels.notifier.external.DebtTestData.*;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_EUR;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_USD;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CustomerDebtDataProvider.class, MockedClockConfiguration.class,
        CustomerDebtDataProviderTest.DebtDataProviderTestConfig.class},
        properties = "external.customer.debt.url=test")
class CustomerDebtDataProviderTest {
    @Autowired
    private CustomerDebtDataProvider provider;

    @Autowired
    private JsonReader mockReader;

    @Test
    void testDebtDataProvider() {
        verify(mockReader).read("test", "content", Debt.class);
        verifyNoMoreInteractions(mockReader);

        assertEquals(EXPECTED_DATE, provider.lastUpdate());

        List<Debt> actualDebtList = provider.get();
        assertEquals(1, actualDebtList.size());

        Debt actual = actualDebtList.get(0);
        RAW_DEBT_MATCHER.assertMatch(actual, debtOne());
        assertEquals(250000.0, actual.getTotalDebt());
        assertEquals(250000.0 * MOCK_RATE_USD, actual.getTotalDebtRouble());
    }

    @TestConfiguration
    static class DebtDataProviderTestConfig {
        @Bean
        public MoexService moexService() {
            MoexService moexService = mock(MoexService.class);
            when(moexService.getUsdRate()).thenReturn(MOCK_RATE_USD);
            when(moexService.getEurRate()).thenReturn(MOCK_RATE_EUR);
            return moexService;
        }

        @Bean
        public JsonReader jsonReader() {
            JsonReader reader = mock(JsonReader.class);
            when(reader.read("test", "content", Debt.class)).thenReturn(List.of(debtOne(), debtTwo()));
            return reader;
        }
    }
}
