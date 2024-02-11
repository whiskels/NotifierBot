package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BeforeEventReportContextTest {

    @Test
    @DisplayName("Should initialize header")
    void testHeaderGeneration() {
        String headerPrefix = "Event in";
        BeforeEventReportContext context = new BeforeEventReportContext(headerPrefix, 5);
        LocalDate testDate = LocalDate.of(2024, 2, 20);
        String expectedHeader = STR."\{headerPrefix} 20-02-2024"; // Adjust based on actual formatting
        assertEquals(expectedHeader, context.getHeaderMapper().apply(testDate));
    }

    @Test
    @DisplayName("Should execute skip data predicate")
    void testBirthdayPredicate() {
        int daysBefore = 5;
        BeforeEventReportContext context = new BeforeEventReportContext("Event in", daysBefore);
        CustomerBirthdayInfo customer = CustomerBirthdayInfo.builder()
                .name("John Doe")
                .birthday(LocalDate.of(2024, 2, 25))
                .build();
        LocalDate reportDate = LocalDate.of(2024, 2, 20);

        // Test case where customer's birthday is exactly 5 days after the report date
        assertTrue(context.getPredicate().test(customer, reportDate));

        // Test case where customer's birthday is not 5 days after the report date
        assertFalse(context.getPredicate().test(customer, reportDate.minusDays(1)));
    }
}