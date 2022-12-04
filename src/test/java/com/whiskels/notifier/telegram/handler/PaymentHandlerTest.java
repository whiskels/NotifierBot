package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.ReportData;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.operation.PaymentDto;
import com.whiskels.notifier.telegram.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.operationRevenue;
import static com.whiskels.notifier.external.json.operation.PaymentDto.from;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import(PaymentHandlerTest.DebtHandlerTestConfig.class)
@SpringBootTest(classes = {PaymentHandler.class})
class PaymentHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_AUTH = format("*Payment report on 22-01-2014*%nTest contractor 2 — 200 USD");

    @Autowired
    private CommandHandler paymentHandler;

    @BeforeEach
    public void setHandler() {
        handler = paymentHandler;
    }

    @Test
    void testPaymentHandler_authorized() {
        testInteraction(USER_1, EXPECTED_AUTH);
    }

    @Test
    void testPaymentHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }

    @TestConfiguration
    static class DebtHandlerTestConfig {
        @Bean
        ReportSupplier<PaymentDto> provider() {
            var mock = mock(ReportSupplier.class);
            when(mock.get()).thenReturn(new ReportData<>(List.of(from(operationRevenue())), EXPECTED_DATE));

            return mock;
        }
    }
}
