package com.whiskels.notifier.reporting.service.employee.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeDataFetchService;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeFeignClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConditionalOnProperty(EmployeeEventFetchConfig.EMPLOYEE_URL)
public class EmployeeEventFetchConfig {
    public static final String EMPLOYEE_URL = "report.parameters.employee-event.url";

    @Bean
    DataFetchService<Employee> employeeDataFetchService(
            final EmployeeFeignClient client,
            final Clock clock
    ) {
        return new EmployeeDataFetchService(client, clock);
    }
}
