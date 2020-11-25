package com.whiskels.notifier.service;

import com.whiskels.notifier.model.Employee;
import com.whiskels.notifier.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whiskels.notifier.model.Employee.STATUS_DECREE;
import static com.whiskels.notifier.model.Employee.STATUS_SYSTEM_FIRED;
import static com.whiskels.notifier.util.DateTimeUtil.toLocalDate;
import static com.whiskels.notifier.util.FormatUtil.*;
import static com.whiskels.notifier.util.StreamUtil.filterAndSort;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService extends AbstractJSONService {
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
                List.of(notDecree(), notFired(), hasBirthday()));
    }

    public String dailyMessage() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        final StringBuilder sb = new StringBuilder();
        sb.append("*Birthdays*\n*" + "Today (")
                .append(DATE_FORMATTER.format(today))
                .append(")*:\n")
                .append(getBirthdayString(isBirthdayOn(today), false))
                .append("\n*Upcoming week:*\n")
                .append(getBirthdayString(isBirthdayNextWeekFrom(today), true));

        return sb.toString();
    }

    public String monthlyMessage() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        return String.format("*Birthdays this month*%n%s",
                getBirthdayString(isBirthdaySameMonth(today), true));
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

    private Predicate<Employee> isBirthdayOn(LocalDate today) {
        return employee -> daysBetweenBirthdayAnd(employee, today) == 0;
    }

    private Predicate<Employee> isBirthdayNextWeekFrom(LocalDate today) {
        return employee -> {
            long daysUntilBirthday = daysBetweenBirthdayAnd(employee, today);
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

    private Predicate<Employee> hasBirthday() {
        return e -> e.getBirthday() != null;
    }
}