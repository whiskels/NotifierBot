package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.operation.dto.PaymentDto;
import com.whiskels.notifier.external.json.operation.service.PaymentSupplier;
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
import static com.whiskels.notifier.external.json.FinOperationTestData.operationRevenue;
import static com.whiskels.notifier.external.json.operation.dto.PaymentDto.fromEntity;
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

    @Autowired
    private Supplier<PaymentDto> paymentSupplier;

    @BeforeEach
    public void setHandler() {
        when(paymentSupplier.getData()).thenReturn(List.of(fromEntity(operationRevenue())));
        when(paymentSupplier.lastUpdate()).thenReturn(EXPECTED_DATE);
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
        Supplier<PaymentDto> provider() {
            return mock(PaymentSupplier.class);
        }
    }
}
