package com.whiskels.notifier.reporting.service.employee.fetch;

import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeDataFetchService;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeDataFetchServiceTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());


    @Mock
    private EmployeeFeignClient employeeClient;

    @InjectMocks
    private EmployeeDataFetchService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeDataFetchService(employeeClient, FIXED_CLOCK);
    }

    @Test
    @DisplayName("Should filter and sort employee data")
    void shouldReturnFilteredAndSortedData() {
        Employee emp1 = new Employee("John", LocalDate.of(2022, 2, 1), LocalDate.of(2022, 1, 1), "Active", "SystemX");
        Employee emp2 = new Employee("Jane", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), null, "SystemY");
        Employee emp3 = new Employee("Jane", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), "Active", null);
        Employee emp4 = new Employee("Doe", LocalDate.of(2022, 3, 1), LocalDate.of(2022, 1, 1), "Декрет", "SystemY");
        Employee emp5 = new Employee("Fired", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 1), "Active", "fired");


        when(employeeClient.get()).thenReturn(List.of(emp1, emp2, emp3, emp4, emp5));

        ReportData<Employee> reportData = service.fetch();

        assertEquals(1, reportData.data().size());
        assertEquals(emp1, reportData.data().getFirst());
        assertEquals(now(FIXED_CLOCK), reportData.requestDate());
    }

    @Test
    @DisplayName("Should return empty employee data")
    void shouldReturnEmptyEmployeeData() {
        when(employeeClient.get()).thenReturn(null);

        ReportData<Employee> reportData = service.fetch();

        assertTrue(reportData.data().isEmpty());
        assertEquals(now(FIXED_CLOCK), reportData.requestDate());
    }
}