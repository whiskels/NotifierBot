package com.whiskels.notifier.external.json.debt;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.currency.CurrencyRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.whiskels.notifier.external.json.currency.RateTestData.MOCK_RATE_REPORT;
import static com.whiskels.notifier.external.json.debt.DebtTestData.*;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtLoaderTest {
    @Mock
    private ReportSupplier<CurrencyRate> currencyRateReportSupplier;

    @Mock
    private DebtFeignClient debtFeignClient;

    @InjectMocks
    private DebtLoader loader;

    @Test
    void testDebtDataLoader() {
        when(currencyRateReportSupplier.get()).thenReturn(MOCK_RATE_REPORT);
        when(debtFeignClient.get()).thenReturn(DebtData.of(List.of(debtOne(), debtTwo())));

        List<Debt> actualList = loader.load();
        assertEquals(1, actualList.size());

        Debt actual = actualList.get(0);
        RAW_DEBT_MATCHER.assertMatch(actual, debtOne());
        assertEquals(valueOf(250000), actual.getTotal());
        assertEquals(valueOf(20000000), actual.getTotalRouble());
    }
}
