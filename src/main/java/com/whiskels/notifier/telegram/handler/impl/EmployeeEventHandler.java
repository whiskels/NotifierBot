package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.annotation.Schedulable;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;
import static com.whiskels.notifier.telegram.Command.GET_EVENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

/**
 * Shows upcoming birthdays to employees
 * <p>
 * Available to: Employee, HR, Manager, Head, Admin
 */
@Slf4j
@BotCommand(command = GET_EVENT, requiredRoles = {EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
@Schedulable(roles = HR)
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class EmployeeEventHandler extends AbstractBaseHandler {
    @Value("${telegram.report.employee.birthday.header:Employee events on}")
    private String header;
    @Value("${telegram.report.employee.birthday.noData:Nobody}")
    private String noData;
    @Value("${telegram.report.employee.birthday:*Birthdays:*}")
    private String upcomingMonth;
    @Value("${telegram.report.employee.anniversary:*Work anniversaries:*}")
    private String anniversary;

    private final DataProvider<Employee> provider;

    public EmployeeEventHandler(AuthorizationService authorizationService,
                                ApplicationEventPublisher publisher,
                                DataProvider<Employee> provider) {
        super(authorizationService, publisher);
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        publish(builder(user)
                .line(ReportBuilder.builder(header + reportDate(provider.lastUpdate()))
                        .setNoData(noData)
                        .setActiveCollector(COLLECTOR_COMMA_SEPARATED)
                        .line(upcomingMonth)
                        .list(filteredBy(Employee::getBirthday, EMPLOYEE_BIRTHDAY_COMPARATOR), Employee::toBirthdayString)
                        .line(anniversary)
                        .list(filteredBy(Employee::getAppointmentDate, EMPLOYEE_ANNIVERSARY_COMPARATOR), Employee::toWorkAnniversaryString)
                        .build())
                .build());
    }

    private List<Employee> filteredBy(Function<Employee, LocalDate> dateFunc, Comparator<Employee> comparator) {
        return filterAndSort(provider,
                comparator,
                notNull(dateFunc),
                isSameMonth(dateFunc, provider.lastUpdate()));
    }
}
