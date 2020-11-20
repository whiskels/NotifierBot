package com.whiskels.notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.notifier.model.Employee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whiskels.notifier.util.TelegramUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService extends AbstractJSONService {
    @Value("${json.employee.url}")
    private String employeeUrl;

    @Getter
    private List<Employee> employeeList;

    private final JSONReader jsonReader;

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
        JSONArray json = (JSONArray) jsonReader.readJsonFromUrl(employeeUrl);
        if (json != null) {
            createEmployeeList(json);
            log.info("employee list updated");
        }
    }

    /**
     * Creates customer list based on JSONArray of objects
     */
    private void createEmployeeList(JSONArray json) {
        employeeList = new ArrayList<>();
        try {
            for (Object o : json) {
                final StringReader reader = new StringReader(o.toString());

                Employee employee = new ObjectMapper().readValue(reader, Employee.class);

                if (employee.getStatusSystem() != null && !employee.getStatusSystem().equalsIgnoreCase(Employee.STATUS_SYSTEM_FIRED) &&
                        employee.getStatus() != null && !employee.getStatus().equalsIgnoreCase(Employee.STATUS_DECREE)) {
                    log.debug(employee.toString());


                    employeeList.add(employee);
                }
            }
        } catch (IOException e) {
            log.error("Exception while reading value from reader - {}", e.getMessage());
        }
    }

    private String getDailyBirthdayInfo(Predicate<Employee> predicate, boolean withDate) {
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

    public String getDailyBirthdayInfo() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        final StringBuilder sb = new StringBuilder();
        sb.append("*Birthdays*%n*" + "Today (")
                .append(DATE_FORMATTER.format(today))
                .append(")*:%n")
                .append(getDailyBirthdayInfo(isBirthday(today), false))
                .append("%n*Upcoming week:*%n")
                .append(getDailyBirthdayInfo(isBirthdayNextWeek(today), true));

        return sb.toString();
    }

    public String getMonthlyBirthdayInfo() {
        final LocalDate today = LocalDateTime.now().plusHours(serverHourOffset).toLocalDate();
        return String.format("*Birthdays this month*%n%s",
                getDailyBirthdayInfo(isBirthdayThisMonth(today), true));
    }
}