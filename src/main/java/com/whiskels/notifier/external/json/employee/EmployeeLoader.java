package com.whiskels.notifier.external.json.employee;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.audit.Audit;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.LoaderType.EMPLOYEE;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.NOT_DECREE;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.NOT_FIRED;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
class EmployeeLoader implements Loader<Employee> {
    private final EmployeeFeignClient employeeClient;

    @SuppressWarnings("unchecked")
    private static final Predicate<Employee>[] EMPLOYEE_FILTERS = new Predicate[]{NOT_DECREE, NOT_FIRED};

    @Override
    @Audit(loader = EMPLOYEE)
    public List<Employee> load() {
        return Optional.ofNullable(employeeClient.get())
                .map(loaded -> filterAndSort(loaded, birthdayComparator(), EMPLOYEE_FILTERS))
                .orElse(emptyList());
    }
}
