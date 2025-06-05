package com.whiskels.notifier.reporting.service.customer.payment.messaging;

import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentReportMessageConverterTest {
    private final PaymentReportMessageConverter converter = new PaymentReportMessageConverter(
            "Payment report on",
            "Nothing",
            Map.of(0, singletonList("Test"), 100000, singletonList("Test2"))
    );

    @Test
    @DisplayName("Should prepare empty report")
    void shouldPrepareEmptyReport() {
        LocalDate regularDate = LocalDate.of(2023, 6, 10);
        ReportData<CustomerPaymentDto> data = new ReportData<>(emptyList(), regularDate);

        var iterable = converter.convert(data);

        var iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        var report = iterator.next();
        assertEquals(Report.builder()
                .header("Payment report on 10-06-2023")
                .notifyChannel(true)
                .build()
                .addBody("Nothing", "Test"), report);
    }

    @Test
    @DisplayName("Should prepare report")
    void shouldPrepareReport() {
        LocalDate regularDate = LocalDate.of(2023, 6, 10);
        CustomerPaymentDto dtoOne = CustomerPaymentDto.builder()
                .amount(new BigDecimal("100000"))
                .amountRub(new BigDecimal("10000000"))
                .currency("EUR")
                .contractor("First entry")
                .build();
        CustomerPaymentDto dtoTwo = CustomerPaymentDto.builder()
                .amount(new BigDecimal("100000000"))
                .amountRub(new BigDecimal("100000000"))
                .currency("RUB")
                .contractor("Second Entry entry")
                .build();
        ReportData<CustomerPaymentDto> data = new ReportData<>(List.of(dtoOne, dtoTwo), regularDate);

        var iterable = converter.convert(data);

        var iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        var report = iterator.next();
        assertEquals(Report.builder()
                        .header("Payment report on 10-06-2023")
                        .notifyChannel(true)
                        .build()
                        .addBody("Second Entry entry — 100 000 000 RUB\nFirst entry — 100 000 EUR", "Test2"),
                report);
    }
}