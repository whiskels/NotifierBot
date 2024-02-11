package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.employee.convert.context.BeforeEventReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("Should execute predicates")
    void testPredicate() {
        int daysBefore = 5;
        BeforeEventReportContext context = new BeforeEventReportContext("Event in", daysBefore);
        Employee employee = new Employee();
        employee.setBirthday(LocalDate.of(2000, 2, 25));
        employee.setAppointmentDate(LocalDate.of(2022, 1, 6));

        assertTrue(context.getBirthdayPredicate().test(employee, LocalDate.of(2024, 2, 20)));
        assertTrue(context.getAnniversaryPredicate().test(employee, LocalDate.of(2023, 1, 1)));

        assertFalse(context.getBirthdayPredicate().test(employee, LocalDate.of(2024, 2, 19)));
        assertFalse(context.getAnniversaryPredicate().test(employee, LocalDate.of(2023, 1, 2)));
    }
}