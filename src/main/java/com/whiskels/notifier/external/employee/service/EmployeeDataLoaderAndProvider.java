package com.whiskels.notifier.external.employee.service;

import com.whiskels.notifier.external.DataLoaderAndProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.external.json.JsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.StreamUtil.filter;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;
import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.employee.url")
public class EmployeeDataLoaderAndProvider implements DataLoaderAndProvider<Employee> {
    private static final Predicate<Employee>[] EMPLOYEE_FILTERS = new Predicate[]{
            NOT_DECREE, NOT_FIRED};

    @Value("${external.employee.url}")
    private String employeeUrl;
    private List<Employee> employeeList;
    private LocalDate lastUpdateDate;

    private final JsonReader jsonReader;
    private final Clock clock;

    @Override
    public List<Employee> get() {
        return employeeList;
    }

    @Override
    public LocalDate lastUpdate() {
        return lastUpdateDate;
    }

    @PostConstruct
    @Scheduled(cron = "${external.employee.cron:0 30 8 * * MON-FRI}", zone = "${common.timezone}")
    public void update() {
        log.info("Updating employee list");
        employeeList = filter(jsonReader.read(employeeUrl, Employee.class), EMPLOYEE_FILTERS);
        lastUpdateDate = now(clock);
    }
}