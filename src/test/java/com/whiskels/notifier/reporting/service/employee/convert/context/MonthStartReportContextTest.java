package com.whiskels.notifier.reporting.service.employee.convert.context;

import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonthStartReportContextTest {

    @Test
    @DisplayName("Should initialize header")
    void testHeaderInitialization() {
        String expectedHeader = "Monthly Start Report";
        MonthStartReportContext context = new MonthStartReportContext(expectedHeader);
        assertEquals(expectedHeader, context.getHeaderMapper().apply(LocalDate.now()));
    }

    @Test
    @DisplayName("Should execute predicates")
    void testPredicates() {
        MonthStartReportContext context = new MonthStartReportContext("Header");
        Employee employee = new Employee();
        employee.setBirthday(LocalDate.of(2000, 2, 25));
        employee.setAppointmentDate(LocalDate.of(2022, 1, 26));
        LocalDate birthdayReportDate = LocalDate.of(2024, 2, 1);
        LocalDate anniversaryReportDate = LocalDate.of(2024, 1, 1);

        // Test case where is same month and after the report date, and the report date is the beginning of the month
        assertTrue(context.getBirthdayPredicate().test(employee, birthdayReportDate));
        assertTrue(context.getAnniversaryPredicate().test(employee, anniversaryReportDate));

        // Test case where is before the report date
        assertFalse(context.getBirthdayPredicate().test(employee, LocalDate.of(2024, 2, 21)));
        assertFalse(context.getAnniversaryPredicate().test(employee, LocalDate.of(2024, 1, 21)));

        // Test case where is not the middle of the month
        assertFalse(context.getBirthdayPredicate().test(employee, LocalDate.of(2024, 2, 10)));
        assertFalse(context.getAnniversaryPredicate().test(employee, LocalDate.of(2024, 1, 10)));

        // Test case where is in a different month
        assertFalse(context.getBirthdayPredicate().test(employee, LocalDate.of(2024, 3, 15)));
        assertFalse(context.getAnniversaryPredicate().test(employee, LocalDate.of(2024, 3, 15)));
    }
}