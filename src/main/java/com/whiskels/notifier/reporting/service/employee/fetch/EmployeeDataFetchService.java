package com.whiskels.notifier.reporting.service.employee.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.domain.HasBirthday;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static com.whiskels.notifier.utilities.collections.StreamUtil.filterAndSort;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class EmployeeDataFetchService implements DataFetchService<Employee> {
    private static final String STATUS_SYSTEM_FIRED = "fired";
    private static final String STATUS_DECREE = "Декрет";
    private static final Predicate<Employee> NOT_FIRED = e -> nonNull(e.getStatusSystem()) && !e.getStatusSystem().equals(STATUS_SYSTEM_FIRED);
    private static final Predicate<Employee> NOT_DECREE = e -> nonNull(e.getStatus()) && !e.getStatus().equals(STATUS_DECREE);

    private final EmployeeFeignClient employeeClient;
    private final Clock clock;

    @AuditDataFetchResult(reportType = EMPLOYEE_EVENT)
    @Nonnull
    @Override
    public ReportData<Employee> fetch() {
        List<Employee> employeeList = Optional.ofNullable(employeeClient.get())
                .map(loaded -> filterAndSort(loaded, HasBirthday.comparator(), NOT_FIRED, NOT_DECREE))
                .orElse(emptyList());
        return new ReportData<>(employeeList, now(clock));
    }
}
