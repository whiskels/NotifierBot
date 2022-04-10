package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.employee.Employee;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.*;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_ANNIVERSARY_COMPARATOR;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.EMPLOYEE_BIRTHDAY_COMPARATOR;
import static com.whiskels.notifier.telegram.Command.GET_EVENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;

@Service
@ConditionalOnBean(value = Employee.class, parameterizedContainer = Supplier.class)
class EmployeeEventHandler implements ScheduledCommandHandler {
    private final String header;
    private final String noData;
    private final String upcomingMonth;
    private final String anniversary;
    private final Supplier<Employee> provider;
    private final SendMessageCreationEventPublisher publisher;

    public EmployeeEventHandler(@Value("${telegram.report.employee.birthday.header:Employee events on}") String header,
                                @Value("${telegram.report.employee.birthday.noData:Nobody}") String noData,
                                @Value("${telegram.report.employee.birthday:*Birthdays:*}") String upcomingMonth,
                                @Value("${telegram.report.employee.anniversary:*Work anniversaries:*}") String anniversary,
                                Supplier<Employee> provider,
                                SendMessageCreationEventPublisher publisher) {
        this.header = header;
        this.noData = noData;
        this.upcomingMonth = upcomingMonth;
        this.anniversary = anniversary;
        this.provider = provider;
        this.publisher = publisher;
    }

    @Override
    @Secured({EMPLOYEE, HR, MANAGER, HEAD, ADMIN})
    public void handle(User user, String message) {
        publisher.publish(builder(user)
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

    @Override
    public Command getCommand() {
        return GET_EVENT;
    }

    @Override
    public Set<Role> getRoles() {
        return Set.of(HR);
    }

    private List<Employee> filteredBy(Function<Employee, LocalDate> dateFunc, Comparator<Employee> comparator) {
        return filterAndSort(provider,
                comparator,
                notNull(dateFunc),
                isSameMonth(dateFunc, provider.lastUpdate()));
    }
}
