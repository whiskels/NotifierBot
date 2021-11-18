package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.employee.Employee;
import com.whiskels.notifier.slack.reporter.AbstractEmployeeEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.*;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = Employee.class, parameterizedContainer = Supplier.class)
public class EmployeeEventReporterAtMonthMiddle extends AbstractEmployeeEventReporter {
    @Value("${slack.employee.header.monthMiddle:Upcoming employee events till the end of the month}")
    private String header;

    public EmployeeEventReporterAtMonthMiddle(@Value("${slack.employee.webhook}") String webHook,
                                              Supplier<Employee> provider,
                                              ApplicationEventPublisher publisher) {
        super(webHook, provider, publisher);
    }

    @Scheduled(cron = "${slack.employee.cron.monthMiddle:0 0 9 15 * *}", zone = "${common.timezone}")
    public void report() {
        createPayload(header);
    }

    protected List<Predicate<Employee>> birthdayPredicates() {
        return generalPredicates(Employee::getBirthday);
    }

    protected List<Predicate<Employee>> anniversaryPredicates() {
        return generalPredicates(Employee::getAppointmentDate);
    }

    private List<Predicate<Employee>> generalPredicates(Function<Employee, LocalDate> func) {
        return List.of(notNull(func),
                isSameMonth(func, provider.lastUpdate()),
                isLaterThan(func, provider.lastUpdate()));
    }
}
