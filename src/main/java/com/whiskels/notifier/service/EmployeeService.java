package com.whiskels.notifier.service;

import com.whiskels.notifier.model.Employee;
import com.whiskels.notifier.util.JsonUtil;
import com.whiskels.notifier.util.ReportBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whiskels.notifier.model.Employee.STATUS_DECREE;
import static com.whiskels.notifier.model.Employee.STATUS_SYSTEM_FIRED;
import static com.whiskels.notifier.util.DateTimeUtil.toLocalDate;
import static com.whiskels.notifier.util.DateTimeUtil.todayWithOffset;
import static com.whiskels.notifier.util.FormatUtil.*;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService extends AbstractJSONService {
    private static final String BIRTHDAY_REPORT_HEADER = "Birthday";
    private static final String BIRTHDAY_MONTHLY_REPORT_HEADER = "Birthday monthly";

    @Value("${json.employee.url}")
    private String employeeUrl;

    @Getter
    private List<Employee> employeeList;

    @PostConstruct
    private void initEmployeeList() {
        update();
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    @Scheduled(cron = "${json.employee.cron}")
    protected void update() {
        log.info("updating employee list");
        employeeList = filterAndSort(readFromJson(employeeUrl),
                List.of(notDecree(), notFired(), isBirthdayNotNull()));
    }

    public String dailyMessage() {
        final LocalDate today = todayWithOffset(serverHourOffset);
        return ReportBuilder.withHeader(BIRTHDAY_REPORT_HEADER, today)
                .line(getBirthdayString(isBirthdayOn(today), false))
                .line("*Upcoming week:*")
                .line(getBirthdayString(isBirthdayNextWeekFrom(today), true))
                .build();
    }

    public String monthlyMessage() {
        final LocalDate today = todayWithOffset(serverHourOffset);
        return ReportBuilder.withHeader(BIRTHDAY_MONTHLY_REPORT_HEADER, today)
                .line(getBirthdayString(isBirthdaySameMonth(today), true))
                .build();
    }

    private List<Employee> readFromJson(String url) {
        return JsonUtil.readValuesFromUrl(url, Employee.class);
    }

    private String getBirthdayString(Predicate<Employee> predicate, boolean withDate) {
        String birthdayInfo = "";
        try {
            Stream<Employee> filteredEmployees = employeeList.stream()
                    .filter(predicate);
            if (withDate) {
                birthdayInfo = filteredEmployees
                        .map(employee -> String.format("%s (%s)",
                                employee.getName(),
                                BIRTHDAY_FORMATTER.format(toLocalDate(employee.getBirthday()))))
                        .collect(Collectors.joining(", "));
            } else {
                birthdayInfo = filteredEmployees
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception e) {
            log.error("Exception while creating message BIRTHDAY: {}", e.getMessage());
        }
        return birthdayInfo.isEmpty() ? "Nobody" : birthdayInfo;
    }

    private long daysBetweenBirthdayAnd(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                toLocalDate(employee.getBirthday()).withYear(today.getYear()).atStartOfDay());
    }

    private Predicate<Employee> isBirthdayOn(LocalDate date) {
        return employee -> daysBetweenBirthdayAnd(employee, date) == 0;
    }

    private Predicate<Employee> isBirthdayNextWeekFrom(LocalDate date) {
        return employee -> {
            long daysUntilBirthday = daysBetweenBirthdayAnd(employee, date);
            return daysUntilBirthday > 0 && daysUntilBirthday <= 7;
        };
    }

    private Predicate<Employee> isBirthdaySameMonth(LocalDate today) {
        return employee -> toLocalDate(employee.getBirthday()).getMonth().equals(today.getMonth());
    }

    private Predicate<Employee> notFired() {
        return e -> !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    }

    private Predicate<Employee> notDecree() {
        return e -> !e.getStatus().equals(STATUS_DECREE);
    }

    private Predicate<Employee> isBirthdayNotNull() {
        return e -> e.getBirthday() != null;
    }
}