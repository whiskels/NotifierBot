package com.whiskels.notifier.reporting.service.employee.fetch;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.whiskels.notifier.reporting.WireMockTestConfig;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.reporting.WireMockTestConfig.setMockResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = "report.parameters.employee-event.url= http://localhost:9561/employees")
@EnableFeignClients(clients = EmployeeFeignClient.class)
@Import({WireMockTestConfig.class})
class EmployeeFeignClientTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Test
    @DisplayName("Should fetch employee data")
    void shouldFetchEmployeeData() {
        String responseBody = """
                [
                    {
                        "name": "John Doe",
                        "birthday": "01.01",
                        "appointment_date": "2022-01-01",
                        "status": "Active",
                        "status_system": "SystemX"
                    }
                ]
                """;
        setMockResponse(wireMockServer, "/employees", responseBody);

        List<Employee> employees = employeeFeignClient.get();

        assertEquals(1, employees.size());
        Employee employee = employees.getFirst();
        assertEquals("John Doe", employee.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 1, 1), employee.getBirthday());
        assertEquals(LocalDate.of(2022, 1, 1), employee.getAppointmentDate());
    }
}
