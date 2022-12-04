package com.whiskels.notifier.external.json.employee;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.read;
import static com.whiskels.notifier.TestUtil.file;
import static com.whiskels.notifier.external.json.employee.EmployeeTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeDeserializationTest {
    @Test
    void testEmployeeDeserialization() {
        List<Employee> employees = read(file(EMPLOYEE_JSON), Employee.class);

        assertThat(employees)
                .containsExactlyInAnyOrder(employeeWorking(), employeeDecree(), employeeFired(), employeeNullBirthday());
    }
}
