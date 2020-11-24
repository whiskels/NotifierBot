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
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whiskels.notifier.model.Employee.STATUS_DECREE;
import static com.whiskels.notifier.model.Employee.STATUS_SYSTEM_FIRED;
import static com.whiskels.notifier.util.FormatUtil.*;

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
    public void update() {
        log.info("updating employee list");
        employeeList = JsonUtil.readValuesFromUrl(employeeUrl, Employee.class).stream()
                .filter(employee -> employee.getBirthday() != null
                        && !employee.getStatus().equals(STATUS_DECREE)
                        && !employee.getStatusSystem().equals(STATUS_SYSTEM_FIRED))
                .collect(Collectors.toList());
    }

    private String getBirthdayString(Predicate<Employee> predicate, boolean withDate) {
        String birthdayInfo = "";
        try {
            if (withDate) {
                birthdayInfo = employeeStream(predicate)
                        .map(employee -> String.format("%s (%s)",
                                employee.getName(),
                                BIRTHDAY_FORMATTER.format(toLocalDate(employee.getBirthday()))))
                        .collect(Collectors.joining(", "));
            } else {
                birthdayInfo = employeeStream(predicate)
                        .map(Employee::getName)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception e) {
            log.error("Exception while creating message BIRTHDAY: {}", e.getMessage());
        }
        return birthdayInfo.isEmpty() ? "Nobody" : birthdayInfo;
    }

    private Stream<Employee> employeeStream(Predicate<Employee> predicate) {
        return employeeList.stream()
                .sorted(Comparator.comparing(Employee::getBirthday))
                .filter(predicate);
    }

    private Predicate<Employee> isBirthday(LocalDate today) {
        return employee -> daysBetweenBirthdayAndToday(employee, today) == 0;
    }

    private Predicate<Employee> isBirthdayNextWeek(LocalDate today) {
        return employee -> {
            long daysUntilBirthday = daysBetweenBirthdayAndToday(employee, today);
            return daysUntilBirthday > 0 && daysUntilBirthday <= 7;
        };
    }

    private Predicate<Employee> isBirthdayThisMonth(LocalDate today) {
        return employee -> toLocalDate(employee.getBirthday()).getMonth().equals(today.getMonth());
    }

    private long daysBetweenBirthdayAndToday(Employee employee, LocalDate today) {
        return ChronoUnit.DAYS.between(
                today.atStartOfDay(),
                toLocalDate(employee.getBirthday()).withYear(today.getYear()).atStartOfDay());
    }

    public String getBirthdayMessage() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        final StringBuilder sb = new StringBuilder();
        sb.append("*Birthdays*\n*" + "Today (")
                .append(DATE_FORMATTER.format(today))
                .append(")*:\n")
                .append(getBirthdayString(isBirthday(today), false))
                .append("\n*Upcoming week:*\n")
                .append(getBirthdayString(isBirthdayNextWeek(today), true));

        return sb.toString();
    }

    public String getMonthlyBirthdayMessage() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        return String.format("*Birthdays this month*%n%s",
                getBirthdayString(isBirthdayThisMonth(today), true));
    }
}