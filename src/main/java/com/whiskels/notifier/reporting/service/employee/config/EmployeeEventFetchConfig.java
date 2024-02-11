package com.whiskels.notifier.reporting.service.employee.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.employee.convert.EmployeeEventReportMessageConverter;
import com.whiskels.notifier.reporting.service.employee.convert.ReportContext;
import com.whiskels.notifier.reporting.service.employee.convert.context.BeforeEventReportContext;
import com.whiskels.notifier.reporting.service.employee.convert.context.DailyReportContext;
import com.whiskels.notifier.reporting.service.employee.convert.context.MonthMiddleReportContext;
import com.whiskels.notifier.reporting.service.employee.convert.context.MonthStartReportContext;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeDataFetchService;
import com.whiskels.notifier.reporting.service.employee.fetch.EmployeeFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;

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
