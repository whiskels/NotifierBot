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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;

@Configuration
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataFetchService.class)
class EmployeeEventReportConfig {

    @Bean
    ReportMessageConverter<Employee> employeeReportMessageConverter(
            List<ReportContext> contexts,
            @Value("${report.parameters.employee-event.no-data:Nobody}") final String noData,
            @Value("${report.parameters.employee-event.birthday-header:*Birthdays:*}") final String birthday,
            @Value("${report.parameters.employee-event.anniversary-header:*Work anniversaries:*}") final String anniversary,
            final Clock clock) {
        return new EmployeeEventReportMessageConverter(contexts, clock, noData, birthday, anniversary);
    }

    @Bean
    ReportContext dailyReportContext(
            @Value("${report.parameters.employee-event.daily.header:\uD83C\uDF89 Employee events on}") final String headerPrefix) {
        return new DailyReportContext(headerPrefix);
    }

    @Bean
    ReportContext monthStartReportContext(
            @Value("${report.parameters.employee-event.month-start.header:\uD83D\uDDD3\uFE0F Upcoming employee events this month}") final String headerPrefix) {
        return new MonthStartReportContext(headerPrefix);
    }

    @Bean
    ReportContext monthMiddleReportContext(
            @Value("${report.parameters.employee-event.month-middle.header:\uD83D\uDDD3\uFE0F Upcoming employee events till end of month}") final String headerPrefix) {
        return new MonthMiddleReportContext(headerPrefix);
    }

    @Bean
    ReportContext daysBeforeReportContext(
            @Value("${report.parameters.employee-event.before.header:⏰ Upcoming employee events in}") final String headerPrefix,
            @Value("${report.parameters.employee-event.before.days:7}") final int daysBefore) {
        return new BeforeEventReportContext(STR."\{headerPrefix} \{daysBefore} days from", daysBefore);
    }

    @Bean
    GenericReportService<Employee> employeeEventReportService(
            DataFetchService<Employee> dataFetchService,
            ReportMessageConverter<Employee> messageCreator
    ) {
        return new GenericReportService<>(EMPLOYEE_EVENT, dataFetchService, messageCreator);
    }
}
