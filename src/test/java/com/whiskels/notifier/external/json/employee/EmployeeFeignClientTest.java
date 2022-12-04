package com.whiskels.notifier.external.json.employee;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.whiskels.notifier.DisabledDataSourceConfiguration;
import com.whiskels.notifier.external.WireMockTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static com.whiskels.notifier.external.WireMockTestConfig.setMockResponse;
import static com.whiskels.notifier.external.json.employee.EmployeeTestData.employeeFired;
import static com.whiskels.notifier.external.json.employee.EmployeeTestData.employeeWorking;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(properties = "external.employee.url: http://localhost:9561/employees")
@EnableFeignClients(clients = EmployeeFeignClient.class)
@ExtendWith(SpringExtension.class)
@Import({DisabledDataSourceConfiguration.class, WireMockTestConfig.class})
class EmployeeFeignClientTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private EmployeeFeignClient employeeFeignClient;

    @Test
    void testEmployeeFeignClient() throws IOException {
        var expected = List.of(employeeWorking(), employeeFired());
        setMockResponse(wireMockServer, "/employees", expected);

        var actual = employeeFeignClient.get();

        assertEquals(expected, actual);
    }
}
