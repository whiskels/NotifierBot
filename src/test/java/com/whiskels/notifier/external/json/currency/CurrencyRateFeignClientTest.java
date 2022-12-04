package com.whiskels.notifier.external.json.currency;

import com.whiskels.notifier.DisabledDataSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@EnableFeignClients(clients = CurrencyRateFeignClient.class)
@Import(DisabledDataSourceConfiguration.class)
class CurrencyRateFeignClientTest {
    @Autowired
    private CurrencyRateFeignClient currencyRateLoader;

    @Test
    void testCurrencyRateFetching() {
        var fetched = currencyRateLoader.get();
        List.of("USD", "EUR", "GBP").forEach(
                currency -> assertNotNull(fetched.getRate(currency))
        );
    }
}
