package com.whiskels.notifier.slack.reporter.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.addWorkingDays;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;

@Component
@Profile("slack-common")
@ConditionalOnProperty("slack.employee.webhook")
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class EmployeeEventReporterDayBeforeEvent extends AbstractEmployeeEventReporter {
    @Value("${slack.employee.header.daily:Upcoming employee events on}")
    private String header;

    public EmployeeEventReporterDayBeforeEvent(@Value("${slack.employee.webhook}") String webHook,
                                               DataProvider<Employee> provider,
                                               ApplicationEventPublisher publisher) {
        super(webHook, provider, publisher);
    }

    @Scheduled(cron = "${slack.employee.cron.daily:0 0 9 * * *}", zone = "${common.timezone}")
    public void report() {
        createPayload(header + reportDate(provider.lastUpdate()), true);
    }

    protected List<Predicate<Employee>> birthdayPredicates() {
        LocalDate startDate = provider.lastUpdate();
        LocalDate endDate = addWorkingDays(provider.lastUpdate(), 1);

        return List.of(BIRTHDAY_NOT_NULL, isBirthdayBetween(startDate, endDate));
    }

    protected List<Predicate<Employee>> anniversaryPredicates() {
        LocalDate startDate = provider.lastUpdate();
        LocalDate endDate = addWorkingDays(provider.lastUpdate(), 1);

        return List.of(isAnniversaryBetween(startDate, endDate));
    }
}
