package com.whiskels.notifier.reporting.service.employee.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
    }

    @Test
    @DisplayName("Should retrieve data via getters")
    void testProperties() {
        employee.setName("John Doe");
        employee.setBirthday(LocalDate.of(1990, 1, 1));
        employee.setAppointmentDate(LocalDate.of(2022, 1, 1));
        employee.setStatus("Active");
        employee.setStatusSystem("SystemX");

        assertEquals("John Doe", employee.getName());
        assertEquals(LocalDate.of(1990, 1, 1), employee.getBirthday());
        assertEquals(LocalDate.of(2022, 1, 1), employee.getAppointmentDate());
        assertEquals("Active", employee.getStatus());
        assertEquals("SystemX", employee.getStatusSystem());
    }

    @Test
    @DisplayName("Should deserialize from json")
    void testDeserialization() throws Exception {
        String json = """
                {
                    "name": "John Doe",
                    "birthday": "01.01",
                    "appointment_date": "2022-01-01",
                    "status": "Active",
                    "status_system": "SystemX"
                }
                """;

        Employee deserializedEmployee = MAPPER.readValue(json, Employee.class);

        assertEquals("John Doe", deserializedEmployee.getName());
        assertEquals(LocalDate.of(2020, 1, 1), deserializedEmployee.getBirthday());
        assertEquals(LocalDate.of(2022, 1, 1), deserializedEmployee.getAppointmentDate());
        assertEquals("Active", deserializedEmployee.getStatus());
        assertEquals("SystemX", deserializedEmployee.getStatusSystem());
    }
}