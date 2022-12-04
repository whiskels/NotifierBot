package com.whiskels.notifier.external.json.currency;

import com.whiskels.notifier.external.ReportData;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.valueOf;
import static java.time.LocalDate.now;

@UtilityClass
public class RateTestData {
    public static final BigDecimal MOCK_RATE_USD = valueOf(0.0125);
    public static final BigDecimal MOCK_RATE_EUR = valueOf(0.0111);

    public static CurrencyRate MOCK_RATES = mockRates();

    public static ReportData<CurrencyRate> MOCK_RATE_REPORT = new ReportData<>(List.of(MOCK_RATES), now());

    private static CurrencyRate mockRates() {
        var rates = new CurrencyRate();
        rates.setRubRates(Map.of("usd", MOCK_RATE_USD, "eur", MOCK_RATE_EUR));
        return rates;
    }
}
