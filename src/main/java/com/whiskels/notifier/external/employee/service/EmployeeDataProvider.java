package com.whiskels.notifier.external.employee.service;

import com.whiskels.notifier.external.ExternalDataProvider;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.external.json.JsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.employee.util.EmployeeUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty("external.employee.url")
public class EmployeeDataProvider implements ExternalDataProvider<Employee> {
    private static final Predicate<Employee>[] EMPLOYEE_FILTERS = new Predicate[]{
            NOT_DECREE, NOT_FIRED, BIRTHDAY_NOT_NULL};

    @Value("${external.employee.url}")
    private String employeeUrl;
    private List<Employee> employeeList;

    private final JsonReader jsonReader;

    @Override
    public List<Employee> get() {
        return employeeList;
    }

    @PostConstruct
    @Scheduled(cron = "${external.employee.cron}", zone = "${common.timezone}")
    public void update() {
        log.info("updating employee list");
        employeeList = filterAndSort(jsonReader.read(employeeUrl, Employee.class), EMPLOYEE_FILTERS);
    }
}