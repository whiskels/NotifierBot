package com.whiskels.notifier.external.debt.scheduler;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.external.DataLoaderAndProvider;
import com.whiskels.notifier.external.debt.DebtInMemoryLoader;
import com.whiskels.notifier.external.debt.Debt;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DebtInMemoryLoader.class, MockedClockConfiguration.class,
        CustomerDebtDataProviderTest.DebtDataProviderTestConfig.class},
        properties = {"external.customer.debt.url=test", "external.customer.debt.json-node=content"})
class CustomerDebtDataProviderTest {
    @Autowired
    private DataLoaderAndProvider<Debt> provider;

    @Autowired
    private JsonReader mockReader;

    @Test
    void testDebtDataProvider() {
        verify(mockReader).read("test", "content", Debt.class);
        verifyNoMoreInteractions(mockReader);

        assertEquals(EXPECTED_DATE, provider.lastUpdate());

        List<Debt> actualDebtList = provider.getData();
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
