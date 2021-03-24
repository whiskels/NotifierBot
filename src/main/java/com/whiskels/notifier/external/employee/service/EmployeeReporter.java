package com.whiskels.notifier.external.employee.service;

import com.whiskels.notifier.common.ReportBuilder;
import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.MonthlyReporter;
import com.whiskels.notifier.external.employee.domain.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;
import static java.time.LocalDate.now;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(value = Employee.class, parameterizedContainer = DataProvider.class)
public class EmployeeReporter implements DailyReporter<Employee>, MonthlyReporter<Employee> {
    private static final String BIRTHDAY_REPORT_HEADER = "Birthdays";
    private static final String BIRTHDAY_MONTHLY_REPORT_HEADER = "Birthdays monthly status";
    private static final String NO_DATA = "Nobody";
    private static final String UPCOMING_WEEK = "*Upcoming week:*";

    private final Clock clock;
    private final DataProvider<Employee> provider;

    public String dailyReport(Predicate<Employee> predicate) {
        final LocalDate today = now(clock);
        final List<Employee> filteredList = filterAndSort(provider.get(), predicate);

        return ReportBuilder.withHeader(BIRTHDAY_REPORT_HEADER, today)
                .setNoData(NO_DATA)
                .list(filteredList, isBirthdayOn(today), COLLECTOR_COMMA_SEPARATED)
                .line()
                .line(UPCOMING_WEEK)
                .list(filteredList, isBirthdayNextWeekFrom(today), COLLECTOR_COMMA_SEPARATED)
                .build();
    }

    public String monthlyReport(Predicate<Employee> predicate) {
        final LocalDate today = now(clock);
        final List<Employee> filteredList = filterAndSort(provider.get(), predicate);

        return ReportBuilder.withHeader(BIRTHDAY_MONTHLY_REPORT_HEADER, today)
                .setNoData(NO_DATA)
                .list(filteredList, isBirthdaySameMonth(today), COLLECTOR_COMMA_SEPARATED)
                .build();
    }
}