package com.whiskels.notifier.external.json.operation;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.whiskels.notifier.DisabledDataSourceConfiguration;
import com.whiskels.notifier.MockedClockConfiguration;
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

import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static com.whiskels.notifier.external.WireMockTestConfig.setMockResponse;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.operationOpex;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.operationRevenue;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = "external.customer.operation.url: http://localhost:9561/fin/startDate/endDate")
@EnableFeignClients(clients = FinOperationFeignClient.class)
@ExtendWith(SpringExtension.class)
@Import({DisabledDataSourceConfiguration.class, WireMockTestConfig.class, MockedClockConfiguration.class})
class FinOperationFeignClientTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private FinOperationFeignClient finOperationFeignClient;

    @Test
    void testFinOperationFeignClient() throws IOException {
        var expected = List.of(operationOpex(), operationRevenue());
        setMockResponse(wireMockServer, "/fin/2014-01-17/2014-01-21", expected);

        var actual = finOperationFeignClient.get();

        assertEqualsIgnoringCR(expected, actual);
    }
}
