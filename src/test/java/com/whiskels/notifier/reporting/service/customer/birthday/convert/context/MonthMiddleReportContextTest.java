package com.whiskels.notifier.reporting.service.customer.birthday.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MonthMiddleReportContextTest {

    @Test
    @DisplayName("Should initialize header")
    void testHeaderMapper() {
        String expectedHeader = "Monthly Report";
        MonthMiddleReportContext context = new MonthMiddleReportContext(expectedHeader);
        assertEquals(expectedHeader, context.getHeaderMapper().apply(LocalDate.now()));
    }

    @Test
    @DisplayName("Should execute skip data predicate")
    void testSkipEmptyPredicate() {
        MonthMiddleReportContext context = new MonthMiddleReportContext("Header");
        assertTrue(context.getSkipEmptyPredicate().test(LocalDate.of(2024, 2, 14)));
        assertFalse(context.getSkipEmptyPredicate().test(LocalDate.of(2024, 2, 15)));
    }

    @Test
    @DisplayName("Should execute birthday predicate")
    void testBirthdayPredicate() {
        MonthMiddleReportContext context = new MonthMiddleReportContext("Header");
        CustomerBirthdayInfo customer = CustomerBirthdayInfo.builder()
                .name("John Doe")
                .birthday(LocalDate.of(1990, 2, 20))
                .build();
        LocalDate reportDate = LocalDate.of(2024, 2, 15);

        // Test case where birthday is in the same month and after the report date, and the report date is the middle of the month
        assertTrue(context.getPredicate().test(customer, reportDate));

        // Test case where birthday is before the report date
        assertFalse(context.getPredicate().test(customer, LocalDate.of(2024, 2, 21)));

        // Test case where report date is not the middle of the month
        assertFalse(context.getPredicate().test(customer, LocalDate.of(2024, 2, 10)));

        // Test case where birthday is in a different month
        assertFalse(context.getPredicate().test(customer, LocalDate.of(2024, 3, 15)));
    }
}