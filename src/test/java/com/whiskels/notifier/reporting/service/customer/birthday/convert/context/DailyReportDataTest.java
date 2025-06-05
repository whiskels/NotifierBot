package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DailyReportDataTest {

    @Test
    @DisplayName("Should initialize header")
    void testHeaderGeneration() {
        String headerPrefix = "Daily Report:";
        DailyReportContext context = new DailyReportContext(headerPrefix);
        LocalDate testDate = LocalDate.of(2024, 2, 20);
        String expectedHeader = STR."\{headerPrefix} 20-02-2024"; // Adjust based on actual formatting
        assertEquals(expectedHeader, context.getHeaderMapper().apply(testDate));
    }

    @Test
    @DisplayName("Should execute birthday predicate")
    void testBirthdayPredicate() {
        DailyReportContext context = new DailyReportContext("Daily Report:");
        CustomerBirthdayInfo customer = CustomerBirthdayInfo.builder()
                .name("Jane Doe")
                .birthday(LocalDate.of(2024, 2, 20))
                .build();
        LocalDate reportDate = LocalDate.of(2024, 2, 20);

        // Test case where customer's birthday is on the report date
        assertTrue(context.getPredicate().test(customer, reportDate));

        // Test case where customer's birthday is not on the report date
        assertFalse(context.getPredicate().test(customer, reportDate.plusDays(1)));
    }
}