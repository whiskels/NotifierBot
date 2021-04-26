package com.whiskels.notifier.external.moex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableConfigurationProperties
@SpringBootTest(properties = {
        "moex.url=https://iss.moex.com/iss/statistics/engines/currency/markets/selt/rates",
        "moex.usd=CBRF_USD_LAST (double)",
        "moex.eur=CBRF_EUR_LAST (double)"
})
@Import(MoexService.class)
class MoexServiceTest {
    @Autowired
    private MoexService moexService;

    @Test
    void testMoexUpdate() {
        moexService.update();
        assertNotNull(moexService.getEurRate());
        assertNotNull(moexService.getUsdRate());
    }
}
