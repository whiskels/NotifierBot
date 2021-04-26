package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;
import static com.whiskels.notifier.telegram.Command.GET_BIRTHDAY;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = GET_BIRTHDAY, requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class BirthdayHandler extends AbstractBaseHandler {
    @Value("${telegram.report.employee.birthday.header:Birthdays on}")
    private String header;
    @Value("${telegram.report.employee.birthday.noData:Nobody}")
    private String noData;
    @Value("${telegram.report.employee.birthday.upcomingWeek:*Upcoming week:*}")
    private String upcomingWeek;
    @Value("${telegram.report.employee.birthday.upcomingMonth:*Upcoming month:*}")
    private String upcomingMonth;
    @Value("${telegram.report.employee.birthday.anniversary:*Work anniversaries this month:*}")
    private String anniversary;

    private final DataProvider<Employee> provider;

    public BirthdayHandler(AuthorizationService authorizationService,
                           ApplicationEventPublisher publisher,
                           DataProvider<Employee> provider) {
        super(authorizationService, publisher);
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        publish(builder(user)
                .line(report())
                .build());
    }

    private String report() {
        final LocalDate reportDate = provider.lastUpdate();
        return reportDate.getDayOfMonth() == 1 ? monthlyReport(reportDate) : monthlyReport(reportDate);
    }

    private String dailyReport(LocalDate reportDate) {
        return reportBuilder(reportDate)
                .list(filterAndSort(provider, EMPLOYEE_BIRTHDAY_COMPARATOR, isBirthdayOn(reportDate)))
                .line(upcomingWeek)
                .list(filterAndSort(provider, EMPLOYEE_BIRTHDAY_COMPARATOR, isBirthdayNextWeekFrom(reportDate)))
                .build();
    }

    private String monthlyReport(LocalDate reportDate) {
        return reportBuilder(reportDate)
                .line(upcomingMonth)
                .list(filterAndSort(provider, EMPLOYEE_BIRTHDAY_COMPARATOR, isBirthdaySameMonth(reportDate)))
                .line(anniversary)
                .list(filterAndSort(provider, EMPLOYEE_ANNIVERSARY_COMPARATOR, isAnniversarySameMonth(reportDate)),
                        Employee::toWorkAnniversaryString)
                .build();
    }

    private ReportBuilder reportBuilder(LocalDate reportDate) {
        return ReportBuilder.builder(header + reportDate(reportDate))
                .setNoData(noData)
                .setActiveCollector(COLLECTOR_COMMA_SEPARATED);
    }
}
