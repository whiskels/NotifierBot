package com.whiskels.notifier.external.json.debt;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.whiskels.notifier.DisabledDataSourceConfiguration;
import com.whiskels.notifier.external.WireMockTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static com.whiskels.notifier.external.WireMockTestConfig.setMockResponse;
import static com.whiskels.notifier.external.json.debt.DebtTestData.debtOne;
import static com.whiskels.notifier.external.json.debt.DebtTestData.debtTwo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = "external.customer.debt.url: http://localhost:9561/debts")
@EnableFeignClients(clients = DebtFeignClient.class)
@ExtendWith(SpringExtension.class)
@Import({DisabledDataSourceConfiguration.class, WireMockTestConfig.class})
class DebtFeignClientTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private DebtFeignClient debtFeignClient;

    @Test
    void testDebtFeignClient() throws IOException {
        var expected = DebtData.of(List.of(debtOne(), debtTwo()));
        setMockResponse(wireMockServer, "/debts", expected);

        var actual = debtFeignClient.get();

        assertEquals(expected, actual);
    }
}
