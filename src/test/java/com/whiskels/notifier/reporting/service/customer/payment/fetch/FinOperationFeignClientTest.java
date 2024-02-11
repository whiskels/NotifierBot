package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.reporting.WireMockTestConfig;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static com.whiskels.notifier.reporting.WireMockTestConfig.setMockResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = "report.parameters.customer-payment.url: http://localhost:9561/operations/startDate/endDate")
@EnableFeignClients(clients = FinOperationFeignClient.class)
@Import({WireMockTestConfig.class, MockedClockConfiguration.class})
class FinOperationFeignClientTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private FinOperationFeignClient finOperationFeignClient;

    @Test
    @DisplayName("Should get financial operations")
    void shouldGetFinancialOperations() {
        String responseBody = """
                [
                    {
                        "id": "1",
                        "currency": "USD",
                        "amount": "1000",
                        "amount_usd": "1000",
                        "category": "Revenue",
                        "contractor": "John Doe"
                    }
                ]
                """;
        setMockResponse(wireMockServer, "/operations/2024-02-20/2024-02-22", responseBody);

        List<FinancialOperation> operations = finOperationFeignClient.get();

        assertEquals(1, operations.size());
        var operation = operations.get(0);
        assertEquals(operation.getCrmId(), 1);
        assertEquals(operation.getCurrency(), "USD");
        assertEquals(operation.getAmount(), BigDecimal.valueOf(1000));
        assertEquals(operation.getAmountUsd(), BigDecimal.valueOf(1000));
        assertEquals(operation.getCategory(), "Revenue");
        assertEquals(operation.getContractor(), "John Doe");
    }
}