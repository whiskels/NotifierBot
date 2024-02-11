package com.whiskels.notifier.reporting.service.employee.convert;

import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDtoTest {

    @Test
    @DisplayName("Should create dto from employee")
    void shouldCreateFromEmployee() {
        Employee employee = new Employee("John Doe", LocalDate.of(1990, 1, 1), LocalDate.of(2015, 1, 1), null, null);
        EmployeeDto dto = EmployeeDto.from(employee);
        assertEquals("John Doe", dto.name());
        assertEquals(LocalDate.of(1990, 1, 1), dto.birthday());
        assertEquals(LocalDate.of(2015, 1, 1), dto.appointmentDate());
    }

    @Test
    @DisplayName("Should map to work anniversary string")
    void testToWorkAnniversaryWithMockedClock() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        EmployeeDto dto = new EmployeeDto("John Doe", LocalDate.of(1990, 1, 1), LocalDate.of(2015, 1, 1));
        String expected = "John Doe 01.01 (9)";
        assertEquals(expected, dto.toWorkAnniversaryString(fixedClock));
    }
}