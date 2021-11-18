package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.JsonReader;
import com.whiskels.notifier.external.moex.MoexLoader;
import com.whiskels.notifier.external.moex.MoexRate;
import com.whiskels.notifier.external.moex.MoexSupplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_EUR;
import static com.whiskels.notifier.external.MoexTestData.MOCK_RATE_USD;
import static com.whiskels.notifier.external.json.DebtTestData.*;
import static com.whiskels.notifier.external.moex.Currency.EUR_RUB;
import static com.whiskels.notifier.external.moex.Currency.USD_RUB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DebtSupplier.class, DebtLoader.class, MoexSupplier.class, MockedClockConfiguration.class,
        CustomerDebtSupplierTest.DebtDataProviderTestConfig.class},
        properties = {"external.customer.debt.url=test", "external.customer.debt.json-node=content"})
class CustomerDebtSupplierTest {
    @Autowired
    private Supplier<Debt> supplier;

    @Autowired
    private JsonReader mockReader;

    @Test
    void testDebtDataProvider() {
        verify(mockReader).read("test", "content", Debt.class);
        verifyNoMoreInteractions(mockReader);

        assertEquals(EXPECTED_DATE, supplier.lastUpdate());

        List<Debt> actualDebtList = supplier.getData();
        assertEquals(1, actualDebtList.size());

        Debt actual = actualDebtList.get(0);
        RAW_DEBT_MATCHER.assertMatch(actual, debtOne());
        assertEquals(250000.0, actual.getTotalDebt());
        assertEquals(250000.0 * MOCK_RATE_USD, actual.getTotalDebtRouble());
    }

    @TestConfiguration
    static class DebtDataProviderTestConfig {
        @Bean
        public MoexLoader moexLoader() {
            MoexLoader moexLoader = mock(MoexLoader.class);
            when(moexLoader.load()).thenReturn(List.of(new MoexRate(USD_RUB, MOCK_RATE_USD), new MoexRate(EUR_RUB, MOCK_RATE_EUR)));
            return moexLoader;
        }

        @Bean
        public JsonReader jsonReader() {
            JsonReader reader = mock(JsonReader.class);
            when(reader.read("test", "content", Debt.class)).thenReturn(List.of(debtOne(), debtTwo()));
            return reader;
        }
    }
}
