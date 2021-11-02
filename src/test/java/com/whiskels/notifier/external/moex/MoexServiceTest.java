package com.whiskels.notifier.external.moex;

import com.whiskels.notifier.MockedClockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableRetry
@EnableConfigurationProperties
@SpringBootTest(properties = {
        "moex.url=https://iss.moex.com/iss/statistics/engines/currency/markets/selt/rates",
        "moex.usd=CBRF_USD_LAST (double)",
        "moex.eur=CBRF_EUR_LAST (double)"
}, classes = {MockedClockConfiguration.class, MoexService.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
class MoexServiceTest {
    @Autowired
    private MoexService moexService;

    @Test
    @Retryable
    void testMoexUpdate() {
        moexService.update();
        assertNotNull(moexService.getEurRate());
        assertNotNull(moexService.getUsdRate());
    }
}
