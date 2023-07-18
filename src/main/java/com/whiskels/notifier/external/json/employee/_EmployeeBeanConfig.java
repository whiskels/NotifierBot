package com.whiskels.notifier.external.json.employee;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.MemoizingReportSupplier;
import com.whiskels.notifier.external.ReportSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Clock;

import static com.whiskels.notifier.external.json.employee._EmployeeBeanConfig.EMPLOYEE_URL;

@Component
@ConditionalOnProperty(EMPLOYEE_URL)
class _EmployeeBeanConfig {
    static final String EMPLOYEE_URL = "external.employee.url";

    @Bean
    Loader<Employee> employeeLoader(EmployeeFeignClient employeeClient) {
        return new EmployeeLoader(employeeClient);
    }

    @Bean
    ReportSupplier<EmployeeDto> employeeDtoSupplier(Clock clock, Loader<Employee> loader) {
        ReportSupplier<Employee> reportSupplier = new MemoizingReportSupplier<>(loader, clock);
        return () -> reportSupplier.get().remap(EmployeeDto::from);
    }
}
