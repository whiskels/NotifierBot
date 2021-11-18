package com.whiskels.notifier.external.moex;

import com.whiskels.notifier.MockedClockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableRetry
@EnableConfigurationProperties
@SpringBootTest(properties = {
        "moex.url=https://iss.moex.com/iss/statistics/engines/currency/markets/selt/rates",
        "moex.usd=CBRF_USD_LAST (double)",
        "moex.eur=CBRF_EUR_LAST (double)"
}, classes = {MockedClockConfiguration.class, MoexLoader.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
class MoexLoaderTest {
    @Autowired
    private MoexLoader moexLoader;

    @Test
    @Retryable
    void testMoexUpdate() {
        assertEquals(2, moexLoader.load().size());
    }
}
