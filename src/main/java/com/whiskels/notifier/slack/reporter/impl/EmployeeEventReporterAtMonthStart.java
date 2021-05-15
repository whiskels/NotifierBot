package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.slack.reporter.AbstractEmployeeEventReporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class EmployeeEventReporterAtMonthStart extends AbstractEmployeeEventReporter {
    @Value("${slack.employee.header.monthStart:Upcoming employee events this month}")
    private String header;

    public EmployeeEventReporterAtMonthStart(@Value("${slack.employee.webhook}") String webHook,
                                             DataProvider<Employee> provider,
                                             ApplicationEventPublisher publisher) {
        super(webHook, provider, publisher);
    }

    @Scheduled(cron = "${slack.employee.cron.monthStart:0 0 9 1 * *}", zone = "${common.timezone}")
    public void report() {
        createPayload(header);
    }

    protected List<Predicate<Employee>> birthdayPredicates() {
        return List.of(BIRTHDAY_NOT_NULL, isBirthdaySameMonth(provider.lastUpdate()));
    }

    protected List<Predicate<Employee>> anniversaryPredicates() {
        return List.of(isAnniversarySameMonth(provider.lastUpdate()));
    }
}
